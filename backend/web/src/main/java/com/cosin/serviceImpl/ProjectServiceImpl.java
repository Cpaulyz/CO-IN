package com.cosin.serviceImpl;

import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.mysql.ProjectMapper;
import com.cosin.dao.neo4j.RelationRepository;
import com.cosin.model.XMLObject.GraphXMLO;
import com.cosin.model.XMLObject.NodeXMLO;
import com.cosin.model.XMLObject.RelationXMLO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import com.cosin.model.po.NodePO;
import com.cosin.model.po.ProjectPO;
import com.cosin.model.po.RelationPO;
import com.cosin.model.dto.ProjectDTO;
import com.cosin.service.ProjectService;
import com.cosin.util.PageHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenyanze
 * @description 知识图谱项目操作
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    NodeRepository nodeRepository;
    @Autowired
    RelationRepository relationRepository;
    @Autowired
    GraphIdRepository graphIdRepository;

    private static XmlMapper xmlMapper;
    static {
        xmlMapper = new XmlMapper();
    }

    /**
     * 创建知识图谱，如果带有xml字段，尝试读取xml导入
     * 注意节点插入需要直接使用repository，而不能通过GraphService中封装的方法，因为GraphService中会从redis获取id进行重设
     * @param projectDTO
     * @return
     * @throws Exception xml读取异常
     */
    @Override
    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public ProjectDTO createProject(ProjectDTO projectDTO) throws Exception {
        ProjectPO projectPO = new ProjectPO(projectDTO);
        // 创建图
        projectMapper.insertProject(projectPO);
        if((projectDTO.getXml()!=null)&&(!projectDTO.getXml().equals(""))){
            GraphXMLO graphXMLO = xmlMapper.readValue(projectDTO.getXml(),GraphXMLO.class);
            System.out.println(graphXMLO);
            checkGraph(graphXMLO); //  throw exception if invalid
            Long maxNodeId = 0L;
            Long maxRelId = 0L;
            // 先加点后加关系，注意要重设projectId，并且记录最大的id，以更新redis
            for(NodeXMLO nodeXMLO:graphXMLO.getNodes()){
                NodePO nodePO = new NodePO(nodeXMLO);
                nodePO.setProjectId(projectPO.getProjectId());
                nodeRepository.save(nodePO);
                maxNodeId = nodeXMLO.getNodeId()>maxNodeId?nodeXMLO.getNodeId():maxNodeId;
            }
            for (RelationXMLO relationXMLO:graphXMLO.getRelations()){
                RelationPO relationPO = new RelationPO(relationXMLO);
                relationPO.setProjectId(projectPO.getProjectId());
                relationRepository.saveBySourceAndTarget(relationPO);
                maxRelId = relationXMLO.getRelationId()>maxRelId?relationXMLO.getRelationId():maxRelId;
            }
            graphIdRepository.setNodeIdByProjectId(projectPO.getProjectId(),maxNodeId); // 更新redis中的id
            graphIdRepository.setRelationIdByProjectId(projectPO.getProjectId(),maxRelId); // 更新redis中的id
        }
        projectDTO.setProjectId(projectPO.getProjectId());
        projectDTO.setXml(""); // 防止返回体过大
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> listProjectByUserId(int userId, int currPage) {
        int currIndex = PageHelper.getIndexByCurrPage(currPage);
        List<ProjectPO> projectPOS = projectMapper.listProjectsByUserId(userId,currIndex,PageHelper.PAGE_SIZE);
        return projectPOS.stream().map(ProjectDTO::new).collect(Collectors.toList());
    }

    @Override
    public int countProjectByUserId(int userId) {
        return projectMapper.countProjectsByUserId(userId);
    }

    @Override
    @CacheEvict(key = "#projectId",value = "projectInfo")
    public boolean updateProjectName(int projectId, String name) {
        int res = projectMapper.updateProjectName(projectId,name);
        return res>0;
    }

    @Override
    @CacheEvict(key = "#projectId",value = "projectInfo")
    public boolean updateProjectDescription(int projectId, String description) {
        int res = projectMapper.updateProjectDescription(projectId,description);
        return res>0;
    }


    /**
     * 根据id导出知识图谱项目，带有xml
     * @param projectId
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public ProjectDTO exportGraphByProjectId(int projectId) throws JsonProcessingException {
        ProjectDTO projectDTO = getProjectById(projectId);
        List<NodeXMLO> nodeXMLOS = nodeRepository.findByProjectId(projectId).stream().map(NodeXMLO::new).collect(Collectors.toList());
        List<RelationXMLO> relationXMLOS = relationRepository.findByProjectId(projectId).stream().map(RelationXMLO::new).collect(Collectors.toList());
        GraphXMLO graphXMLO = new GraphXMLO();
        graphXMLO.setNodes(nodeXMLOS);
        graphXMLO.setRelations(relationXMLOS);
        String res = xmlMapper.writeValueAsString(graphXMLO);
        projectDTO.setXml(res);
        return projectDTO;
    }

    @Override
    @CacheEvict(key = "#projectId",value = "projectInfo")
    public boolean updateProjectLayout(int projectId, LayoutTypeEnum layout) {
        int res = projectMapper.updateProjectLayout(projectId,layout);
        return res>0;
    }


    /**
     * 根据projectId获取项目信息
     * @param projectId
     * @return
     */
    @Override
    @Cacheable(key = "#projectId",value = "projectInfo")
    public ProjectDTO getProjectById(int projectId) {
        ProjectPO projectPO = projectMapper.getProjectByProjectId(projectId);
        return new ProjectDTO(projectPO);
    }

    @Override
    @CacheEvict(key = "#projectId",value = "projectInfo")
    public boolean updateProjectStatus(int projectId, ProjectStatusEnum projectStatusEnum) {
        int res = projectMapper.updateProjectStatus(projectId,projectStatusEnum);
        return res>0;
    }

    @Override
    public List<ProjectDTO> listPublicProjects(int currPage) {
        int currIndex = PageHelper.getIndexByCurrPage(currPage);
        List<ProjectPO> projectPOS = projectMapper.listPublicProjects(currIndex,PageHelper.PAGE_SIZE);
        return projectPOS.stream().map(ProjectDTO::new).collect(Collectors.toList());
    }

    @Override
    public int countPublicProject() {
        return projectMapper.countPublicProject();
    }


    /**
     * 判断传入的graph是否有效，可能无效的情况有：
     * 1. 点id重复
     * 2. 边id重复
     * 3. 边source或target的id不存在
     * @param graphXMLO
     * @return
     */
    private void checkGraph(GraphXMLO graphXMLO) throws Exception{
        HashSet<Long> nodeSet = new HashSet<>(); // 存放所以点id的组合
        HashSet<Long> relSet = new HashSet<>(); // 存放所以边id的组合
        for(NodeXMLO node:graphXMLO.getNodes()){
            if(nodeSet.contains(node.getNodeId())){
                throw new Exception("nodeId重复: "+node.getNodeId());
            }
            nodeSet.add(node.getNodeId());
        }
        for(RelationXMLO relation:graphXMLO.getRelations()){
            if(relSet.contains(relation.getRelationId())){
                throw new Exception("relationId重复: "+relation.getRelationId());
            }else if(!nodeSet.contains(relation.getSource())){
                throw new Exception("relation关联节点未找到: "+relation.getRelationId());
            }
            relSet.add(relation.getRelationId());
        }
    }
}
