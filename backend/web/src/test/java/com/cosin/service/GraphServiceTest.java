package com.cosin.service;

import com.cosin.COINWebApplication;
import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.neo4j.RelationRepository;
import com.cosin.model.po.NodePO;
import com.cosin.model.po.RelationPO;
import com.cosin.model.dto.*;
import com.cosin.model.vo.GraphVO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.EntityQueryVO;
import com.cosin.model.vo.node.NodeRelNodeVO;
import com.cosin.model.vo.node.NodeRelVO;
import com.cosin.serviceImpl.GraphServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class GraphServiceTest {

    @Autowired
    GraphServiceImpl graphService;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    RelationRepository relationRepository;

    @Autowired
    GraphIdRepository graphIdRepository;

    NodePO nodePO1;
    NodePO nodePO2;
    NodeDTO nodeDTO;
    RelationPO relationPO;
    RelationDTO relationDTO;

    private static final int TEST_PROJECT_ID = 0;
    @Before
    public void resetGeneratorAndSave(){
        relationRepository.deleteByProjectId(TEST_PROJECT_ID);
        nodeRepository.deleteByProjectId(TEST_PROJECT_ID);
        System.out.println("=========reset generator==========");
        graphIdRepository.setNodeIdByProjectId(TEST_PROJECT_ID,0L);
        graphIdRepository.setRelationIdByProjectId(TEST_PROJECT_ID,0L);
        nodePO1 = new NodePO().setNodeId(graphIdRepository.generateNodeIdByProjectId(TEST_PROJECT_ID)).setName("皮卡丘").setGroup("宠物")
                .setRadius(3).setColor("color").setFigure("figure").setTextSize("12").setProjectId(TEST_PROJECT_ID);
        nodePO2 = new NodePO().setNodeId(graphIdRepository.generateNodeIdByProjectId(TEST_PROJECT_ID)).setName("小智").setGroup("人物")
                .setRadius(3).setColor("color").setFigure("figure").setTextSize("12").setProjectId(TEST_PROJECT_ID);
        NodePO node1 =nodeRepository.save(nodePO1);
        NodePO node2 =nodeRepository.save(nodePO2);

        relationPO = new RelationPO()
                .setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setName("主人")
                .setSource(node2.getNodeId()).setTarget(node1.getNodeId())
                .setSourceNode(node2).setTargetNode(node1).setProjectId(TEST_PROJECT_ID);

        RelationPO relationPO1 = relationRepository.saveBySourceAndTarget(relationPO);
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(relationPO1);
        System.out.println("=========insert end==========");


        Map<String,String> props = new HashMap<>();
        props.put("prop","value");
        nodeDTO = new NodeDTO().setProjectId(TEST_PROJECT_ID).setName("test").setGroup("test").setRadius(1)
                .setColor("default").setTextSize("15").setProperties(props);
        relationDTO = new RelationDTO().setSource(node1.getNodeId()).setTarget(node2.getNodeId()).setName("test").setWidth(1).setProjectId(TEST_PROJECT_ID);
    }

    @After
    public void deleteAll(){
        System.out.println("=========finish and delete all==========");
        relationRepository.deleteByProjectId(TEST_PROJECT_ID);
        nodeRepository.deleteByProjectId(TEST_PROJECT_ID);
        graphIdRepository.setNodeIdByProjectId(TEST_PROJECT_ID,0L);
        graphIdRepository.setRelationIdByProjectId(TEST_PROJECT_ID,0L);
    }

    @Test
    public void testGetGraph(){
        GraphVO graphVO = graphService.getGraphByProjectId(TEST_PROJECT_ID);
        assert graphVO.getProjectId()==TEST_PROJECT_ID;
        assert graphVO.getNodes().size()!=0;
        assert graphVO.getRelations().size()!=0;
        for(NodeDTO nodeDTO :graphVO.getNodes()){
            assert nodeDTO.getProjectId()==TEST_PROJECT_ID;
        }
        for(RelationDTO relationDTO :graphVO.getRelations()){
            assert relationDTO.getProjectId()==TEST_PROJECT_ID;
        }
    }

    @Test
    public void testInsertNode(){
        NodeDTO res = graphService.insertNode(nodeDTO);
        System.out.println("testInsertNode->"+res);
        assert res!=null;
    }

    @Test
    public void testInsertRel(){
        RelationDTO res = graphService.insertRelation(relationDTO);
        System.out.println("testInsertRel->"+res);
        assert res!=null;
    }

    @Test
    public void testUpdateNode(){
        NodeDTO n = new NodeDTO(nodePO1).setName("modify").setGroup("modify").setRadius(2).setFigure("f").setTextSize("20");
        NodeDTO res = graphService.updateNode(n);
        System.out.println("testUpdateNode->"+res);
        assert res!=null;
    }

    @Test
    public void testUpdateRel(){
        RelationDTO r= new RelationDTO(relationPO).setName("modify").setWidth(2);
        System.out.println("RelationDTO->"+r);
        RelationDTO res = graphService.updateRelation(r);
        System.out.println("testUpdateRel->"+res);
        assert res!=null;
    }

    @Test
    public void testDeleteNodeSuccess(){
        NodeDTO res = graphService.insertNode(nodeDTO);
        ResponseVO responseVO = graphService.deleteNode(res);
        assert responseVO.isSuccess();
    }

    @Test
    public void testDeleteNodeFail(){
        ResponseVO responseVO = graphService.deleteNode(new NodeDTO(nodePO1));
        assert !responseVO.isSuccess();
    }

    @Test
    public void testCascadeDeleteNode(){
        NodeRelVO nodeRelVO = graphService.cascadeDeleteNode(new NodeDTO(nodePO1));
        assert nodeRelVO.getNodeId()==nodePO1.getNodeId();
        assert nodeRelVO.getRelations().size()==1;
    }

    @Test
    public void testDeleteRel(){
        ResponseVO responseVO = graphService.deleteRelation(new RelationDTO(relationPO));
        assert responseVO.isSuccess();
    }

    @Test
    public void testEntityQuery(){
        EntityQueryVO entityQueryVO = new EntityQueryVO().setProjectId(TEST_PROJECT_ID).setEntityName("皮卡").setFuzzyQuery(true);
        GraphVO graphVO = graphService.entityQuery(entityQueryVO);
        assert graphVO.getProjectId()==TEST_PROJECT_ID;
        assert graphVO.getNodes().size()!=0;
        assert graphVO.getRelations().size()!=0;
        for(NodeDTO nodeDTO :graphVO.getNodes()){
            assert nodeDTO.getProjectId()==TEST_PROJECT_ID;
        }
        for(RelationDTO relationDTO :graphVO.getRelations()){
            assert relationDTO.getProjectId()==TEST_PROJECT_ID;
        }
    }

    @Test
    public void testRelQuery(){
        NodeRelNodeVO nodeRelNodeVO = new NodeRelNodeVO().setProjectId(TEST_PROJECT_ID).setRelName("主人").setSourceName("").setTargetName("");
        GraphVO graphVO = graphService.relQuery(nodeRelNodeVO);
        assert graphVO.getProjectId()==TEST_PROJECT_ID;
        assert graphVO.getNodes().size()!=0;
        assert graphVO.getRelations().size()!=0;
        for(NodeDTO nodeDTO :graphVO.getNodes()){
            assert nodeDTO.getProjectId()==TEST_PROJECT_ID;
        }
        for(RelationDTO relationDTO :graphVO.getRelations()){
            assert relationDTO.getProjectId()==TEST_PROJECT_ID;
        }
    }

    @Test
    public void testFindRelationNameByProjectId(){
        List<String> res = relationRepository.findRelationNameByProjectId(TEST_PROJECT_ID);
        assert res.size()>0;
        System.out.println(res);
    }
}
