package com.cosin.serviceImpl;

import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.neo4j.RelationRepository;
import com.cosin.model.enums.ProjectStatusEnum;
import com.cosin.model.po.NodePO;
import com.cosin.model.po.RelationPO;
import com.cosin.model.dto.*;
import com.cosin.model.vo.GraphVO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.EntityQueryVO;
import com.cosin.model.vo.node.NodePairVO;
import com.cosin.model.vo.node.NodeRelNodeVO;
import com.cosin.model.vo.node.NodeRelVO;
import com.cosin.service.GraphService;
import com.cosin.service.LayoutService;
import com.cosin.service.ProjectService;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Chenyz
 * @Description: 知识图谱操作相关服务
 */
@Service
public class GraphServiceImpl implements GraphService {

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    RelationRepository relationRepository;

    @Autowired
    GraphIdRepository graphIdRepository;

    @Autowired
    LayoutService layoutService;

    @Autowired
    ProjectService projectService;

    /**
     * 获取图信息
     */
    @Override
    public GraphVO getGraphByProjectId(int projectId) {
        GraphVO graphVO = new GraphVO();
        List<NodeDTO> nodeDTOS = nodeRepository.findByProjectId(projectId).stream().map(NodeDTO::new).collect(Collectors.toList());
        List<RelationDTO> relationDTOS = relationRepository.findByProjectId(projectId).stream().map(RelationDTO::new).collect(Collectors.toList());
        List<LayoutDTO> layoutDTOS = layoutService.getLayoutByProjectId(projectId);
        graphVO.setProjectId(projectId).setNodes(nodeDTOS).setRelations(relationDTOS).setLayout(layoutDTOS);
        return graphVO;
    }

    @Override
    public NodeDTO insertNode(NodeDTO nodeDTO) {
        NodePO nodePO = new NodePO(nodeDTO);
        nodePO.setNodeId(graphIdRepository.generateNodeIdByProjectId(nodePO.getProjectId()));
        NodePO res = nodeRepository.save(nodePO);
        return new NodeDTO(res);
    }

    @Override
    public RelationDTO insertRelation(RelationDTO relationDTO) throws NoSuchElementException {
        RelationPO relationPO = new RelationPO(relationDTO);
        boolean tmp = fillSourceAndTargetNode(relationPO);
        if (!tmp) {
            return null;
        }
        relationPO.setRelationId(graphIdRepository.generateRelationIdByProjectId(relationPO.getProjectId()));
//        RelationPO relationPO1 = relationRepository.saveBySourceAndTarget(relationPO);
        RelationPO relationPO1 = relationRepository.save(relationPO);
        return new RelationDTO(relationPO1);
    }

    @Override
    public NodeDTO updateNode(NodeDTO nodeDTO) {
        NodePO currNode = nodeRepository.findByNodeIdAndProjectId(nodeDTO.getNodeId(), nodeDTO.getProjectId());
        NodePO nodePO = nodeRepository.save(new NodePO(nodeDTO).setId(currNode.getId()));
        return new NodeDTO(nodePO);
    }


    @Override
    public RelationDTO updateRelation(RelationDTO relationDTO) {
//        RelationPO relationPO1 = relationRepository.updateByRel(new RelationPO(relationDTO));
        RelationPO relationPO = new RelationPO(relationDTO);
        relationPO.setId(relationRepository.findIdByRelationIdAndProjectId(relationPO.getRelationId(), relationPO.getProjectId()));
//        System.out.println(relationPO);
        boolean tmp = fillSourceAndTargetNode(relationPO);
        if (!tmp) {
            return null;
        }
        RelationPO relationPO1 = relationRepository.save(relationPO);
//        System.out.println(relationPO1);
        return new RelationDTO(relationPO1);
    }

    /**
     * 填充sourceNode和targetNode
     *
     * @param relationPO
     */
    private boolean fillSourceAndTargetNode(RelationPO relationPO) {
        NodePO src = nodeRepository.findByNodeIdAndProjectId(relationPO.getSource(), relationPO.getProjectId());
        if (src == null) {
            return false;
        } else {
            relationPO.setSourceNode(src);
        }
        NodePO tar = nodeRepository.findByNodeIdAndProjectId(relationPO.getTarget(), relationPO.getProjectId());
        if (tar == null) {
            return false;
        } else {
            relationPO.setTargetNode(tar);
        }
        return true;
    }

    /**
     * 非级联删除，如果带有节点会抛出异常
     * 注意：要删除layout
     */
    @Override
    public ResponseVO deleteNode(NodeDTO nodeDTO) {
        try {
            nodeRepository.deleteByNode(new NodePO(nodeDTO));
            layoutService.deleteLayoutByNodeIdAndProjectId(nodeDTO.getNodeId(), nodeDTO.getProjectId());
        } catch (Exception e) {
            return ResponseVO.buildFailure("Cannot delete node, because it still has relationships. To delete this node, you must first delete its relationships..");
        }
        return ResponseVO.buildSuccess();
    }

    /**
     * 级联删除节点，返回节点及关联关系
     * 注意：要删除layout
     */
    @Override
    public NodeRelVO cascadeDeleteNode(NodeDTO nodeDTO) {
        NodePO nodePO = new NodePO(nodeDTO);
        NodeRelVO res = new NodeRelVO();
        List<RelationPO> relationPOS = relationRepository.listBySourceOrTarget(nodePO);
        nodeRepository.cascadeDeleteNode(new NodePO(nodeDTO));
        layoutService.deleteLayoutByNodeIdAndProjectId(nodeDTO.getNodeId(), nodeDTO.getProjectId());
        res.setProjectId(nodeDTO.getProjectId());
        res.setNodeId(nodeDTO.getNodeId());
        List<Long> rels = new ArrayList<>();
        relationPOS.forEach(i -> rels.add(i.getRelationId()));
        res.setRelations(rels);
        return res;
    }

    @Override
    public ResponseVO deleteRelation(RelationDTO relationDTO) {
        relationRepository.deleteByRelation(new RelationPO(relationDTO));
        return ResponseVO.buildSuccess();
    }

    @Override
    public GraphVO entityQuery(EntityQueryVO entityQueryVO) {
        entityQueryVO.setEntityName(entityQueryVO.isFuzzyQuery() ? transferFuzzy(entityQueryVO.getEntityName()) : entityQueryVO.getEntityName());
        List<RelationPO> relationPOS = relationRepository.findBySourceName(entityQueryVO.getProjectId(), entityQueryVO.getEntityName());
        return getGraphFromRelation(entityQueryVO.getProjectId(), relationPOS);
    }

    @Override
    public GraphVO relQuery(NodeRelNodeVO nodeRelNodeVO) {
        /**
         * 约束: 必有r
         * 情况1. n-r->m 都有
         * 情况2. n-r->?
         * 情况3. ?-r->m
         * 情况4. ?-r->?
         */
        if ("".equals(nodeRelNodeVO.getRelName())) {
            return null; // 不符合约束，错误
        }
        String defaultQuery = ".*";
        // replace by fuzzy query
        String from = "".equals(nodeRelNodeVO.getSourceName()) ? defaultQuery : nodeRelNodeVO.getSourceName();
        String to = "".equals(nodeRelNodeVO.getTargetName()) ? defaultQuery : nodeRelNodeVO.getTargetName();
        List<RelationPO> relationPOS = relationRepository.findByProjectIdAndNameAndTargetAndSourceFuzzy(
                nodeRelNodeVO.getProjectId(), nodeRelNodeVO.getRelName(), from, to);
        return getGraphFromRelation(nodeRelNodeVO.getProjectId(), relationPOS);
    }

    @Override
    public List<String> findRelationNameByProjectId(int projectId) {
        return relationRepository.findRelationNameByProjectId(projectId);
    }

    @Override
    public GraphVO getGraphFromNodePair(int projectId, List<NodePairVO> nodePairs) {
        List<Long> isolateNodes = new ArrayList<>(); // 孤立点，即只有src，没有tar
        List<RelationPO> relationPOS = new ArrayList<>(); // 相关的relation
        HashSet<Long> nodeIds = new HashSet<>(); // 去重用
        for (NodePairVO nodePairVO : nodePairs) {
            if (nodePairVO.getTarget() > 0) {
                RelationPO tmp = relationRepository.findByProjectIdAndTargetIdAndSourceId(projectId, nodePairVO.getSrc(), nodePairVO.getTarget());
//                System.out.println(tmp);
                if (tmp != null) {
                    relationPOS.add(tmp);
                    nodeIds.add(tmp.getSourceNode().getNodeId());
                    nodeIds.add(tmp.getTargetNode().getNodeId());
                }
            } else {
                isolateNodes.add((long) nodePairVO.getSrc());
            }
        }
        GraphVO res = getGraphFromRelation(projectId, relationPOS);
        // add isolate node
        for (long nodeId : isolateNodes) {
            if (!nodeIds.contains(nodeId)) {
                NodePO nodePO = nodeRepository.findByNodeIdAndProjectId(nodeId, projectId);
                res.getNodes().add(new NodeDTO(nodePO));
                // TODO：不知道如果没有layout的话，前端能不能正常显示？记得是可以的，如果不行的话这里再改
            }
        }
        return res;
    }

    @Override
    public String projectQuery(String sentence) {
        HashMap<Integer, List<String>> map = new HashMap<>();
        List<Term> termList = StandardTokenizer.segment(sentence);
        for (Term t : termList) {
            if (t.nature.startsWith('n')) { // 提取名词
                System.out.println("nword:" + t.word);
                List<NodePO> nodePOS = nodeRepository.findByNameFuzzy(transferFuzzy(t.word));
                for (NodePO node : nodePOS) {
                    if (!map.containsKey(node.getProjectId())) {
                        map.put(node.getProjectId(), new ArrayList<>());
                    }
                    map.get(node.getProjectId()).add(node.getName());
                }
            }
        }
        String res = "";
        for (int projectId : map.keySet()) {
            ProjectDTO project = projectService.getProjectById(projectId);
            if (project.getStatus() == ProjectStatusEnum.PUBLIC) {
                res += transferToHtml(project.getName(), projectId);
                res += "中有";
                res += StringUtils.join(map.get(projectId), '、');
                res += "。\\n";
            }
        }
        if ("".equals(res)) {
            res = "小助手没有找到相关项目 :(";
        } else {
            res = "小助手为您找到：\\n" + res;
        }
        System.out.println(res);
        return res;
    }

    @Override
    public List<Integer> projectQueryNew(String sentence) {
        HashMap<Integer, List<String>> map = new HashMap<>();
        List<Term> termList = StandardTokenizer.segment(sentence);
        for (Term t : termList) {
            if (t.nature.startsWith('n')) { // 提取名词
                System.out.println("nword:" + t.word);
                List<NodePO> nodePOS = nodeRepository.findByNameFuzzy(transferFuzzy(t.word));
                for (NodePO node : nodePOS) {
                    if (!map.containsKey(node.getProjectId())) {
                        map.put(node.getProjectId(), new ArrayList<>());
                    }
                    map.get(node.getProjectId()).add(node.getName());
                }
            }
        }
        List<Integer> res = new ArrayList<>();
        for (int projectId : map.keySet()) {
            ProjectDTO project = projectService.getProjectById(projectId);
            if (project.getStatus() == ProjectStatusEnum.PUBLIC) {
                res.add(projectId);
            }
        }
        return res;
    }

    /**
     * 抽取的公共方法，由relationPOS生成GraphVO
     *
     * @param projectId   项目id
     * @param relationPOS 必须带有sourceNode和targetNode
     * @return
     */
    private GraphVO getGraphFromRelation(int projectId, List<RelationPO> relationPOS) {
        GraphVO graphVO = new GraphVO();
        HashSet<Long> nodeIdSet = new HashSet<>(); // 用于判断是否已经加入过
        List<NodeDTO> nodeDTOS = new ArrayList<>();
        List<RelationDTO> relationDTOS = new ArrayList<>();
        // 遍历relationPO，从中挖取节点和关系信息，用哈希表去重
        for (RelationPO relationPO : relationPOS) {
            relationDTOS.add(new RelationDTO(relationPO));
            if (!nodeIdSet.contains(relationPO.getSourceNode().getNodeId())) {
                nodeIdSet.add(relationPO.getSourceNode().getNodeId());
                nodeDTOS.add(new NodeDTO(relationPO.getSourceNode()));
            }
            if (!nodeIdSet.contains(relationPO.getTargetNode().getNodeId())) {
                nodeIdSet.add(relationPO.getTargetNode().getNodeId());
                nodeDTOS.add(new NodeDTO(relationPO.getTargetNode()));
            }
        }
        List<Long> nodeIds = new ArrayList<>(nodeIdSet);
        List<LayoutDTO> layoutDTOS = layoutService.getLayoutByProjectIdAndNodeId(projectId, nodeIds);
        graphVO.setProjectId(projectId).setNodes(nodeDTOS).setRelations(relationDTOS).setLayout(layoutDTOS);
        return graphVO;
    }

    /**
     * 转换为模糊匹配
     *
     * @param name 原字段
     * @return 模糊字段
     */
    private String transferFuzzy(String name) {
        return ".*" + name + ".*";
    }

    /**
     * 转为<a>标签包着的答案</a>
     *
     * @param name
     * @param projectId
     * @return
     */
    private String transferToHtml(String name, int projectId) {
        return "<a href=\"#/smarthelper/" + projectId + "\">" + name + "</a>";
    }
}
