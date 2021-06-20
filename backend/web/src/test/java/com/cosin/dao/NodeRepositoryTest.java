package com.cosin.dao;

import com.cosin.COINWebApplication;
import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.neo4j.RelationRepository;
import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.model.po.NodePO;
import com.cosin.model.po.NodeWithRelationPO;
import com.cosin.model.po.RelationPO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NodeRepositoryTest {
    @Autowired
    NodeRepository nodeRepository;
    @Autowired
    GraphIdRepository graphIdRepository;
    @Autowired
    RelationRepository relationRepository;

    private static final int TEST_PROJECT_ID = 0;
    @Before
    public void resetGeneratorAndSave(){
        relationRepository.deleteByProjectId(TEST_PROJECT_ID);
        nodeRepository.deleteByProjectId(TEST_PROJECT_ID);
        System.out.println("=========reset generator==========");
        graphIdRepository.setNodeIdByProjectId(TEST_PROJECT_ID,0L);
        graphIdRepository.setRelationIdByProjectId(TEST_PROJECT_ID,0L);
        NodePO nodePO1 = new NodePO().setNodeId(graphIdRepository.generateNodeIdByProjectId(TEST_PROJECT_ID)).setName("皮卡丘").setGroup("宠物")
                .setRadius(3).setColor("color").setFigure("figure").setTextSize("12").setProjectId(TEST_PROJECT_ID);
        NodePO nodePO2 = new NodePO().setNodeId(graphIdRepository.generateNodeIdByProjectId(TEST_PROJECT_ID)).setName("小智").setGroup("人物")
                .setRadius(3).setColor("color").setFigure("figure").setTextSize("12").setProjectId(TEST_PROJECT_ID);
        Map<String,String> properties = new HashMap<>();
        properties.put("生日","12-20");
        properties.put("身高","120");
        nodePO1.setProperties(properties);
        NodePO node1 =nodeRepository.save(nodePO1);
        NodePO node2 =nodeRepository.save(nodePO2);
        System.out.println(node1);
        System.out.println(node2);
        System.out.println("=========insert end==========");
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
    public void testSave(){
        NodePO nodePO1 = new NodePO().setNodeId(graphIdRepository.generateNodeIdByProjectId(TEST_PROJECT_ID)).setName("皮卡丘").setGroup("宠物")
                .setRadius(4).setColor("color1").setFigure("figure1").setTextSize("13").setProjectId(TEST_PROJECT_ID);
        Map<String,String> properties = new HashMap<>();
        properties.put("生日","12-20");
        properties.put("身高","120");
        nodePO1.setProperties(properties);
        NodePO node1 =nodeRepository.save(nodePO1);
        System.out.println(node1);
        nodePO1.setId(node1.getId());
        assert node1.equals(nodePO1);
    }

    @Test
    public void testListByProjectId(){
        List<NodePO> nodePOList = nodeRepository.findByProjectId(TEST_PROJECT_ID);
        for(NodePO nodePO:nodePOList){
            assert nodePO.getProjectId()==TEST_PROJECT_ID;
        }
    }

    @Test
    public void testSaveUpdate(){
        NodePO currNode = nodeRepository.findByNodeIdAndProjectId(1L,TEST_PROJECT_ID);
        NodePO nodePO1 = new NodePO().setId(currNode.getId()).setNodeId(1L).setName("皮卡皮卡").setGroup("修改")
                .setRadius(4).setColor("color1").setFigure("figure1").setTextSize("13").setProjectId(TEST_PROJECT_ID);
        NodePO nodePO = nodeRepository.save(nodePO1);
        System.out.println(nodePO);
        assert nodePO.getName().equals(nodePO1.getName());
        assert nodePO.getRadius()==nodePO1.getRadius();
        assert nodePO.getGroup().equals(nodePO1.getGroup());
        assert nodePO.getColor().equals(nodePO1.getColor());
        assert nodePO.getFigure().equals(nodePO1.getFigure());
        assert nodePO.getTextSize().equals(nodePO1.getTextSize());
    }

    @Test
    public void testDelete(){
        NodePO nodePO = new NodePO();
        nodePO.setNodeId(1L).setProjectId(TEST_PROJECT_ID);
        nodeRepository.deleteByNode(nodePO);
        List<NodePO> nodePOList = nodeRepository.findByProjectId(TEST_PROJECT_ID);
        boolean res= true;
        for(NodePO nodePO1:nodePOList){
            if (nodePO1.getNodeId()==1){
                res = false;
                break;
            }
        }
        assert res;
    }

    @Test(expected = Exception.class)
    public void testDeleteFailed(){
        RelationPO relationPO = new RelationPO();
        relationPO.setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setProjectId(TEST_PROJECT_ID).setSource(1L).setTarget(2L);
        relationRepository.saveBySourceAndTarget(relationPO);
        NodePO nodePO = new NodePO();
        nodePO.setNodeId(1L).setProjectId(TEST_PROJECT_ID);
        nodeRepository.deleteByNode(nodePO);
    }

    @Test
    public void testCascadeDeleteNode(){
        RelationPO relationPO = new RelationPO();
        relationPO.setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setProjectId(TEST_PROJECT_ID)
                .setName("test").setSource(1L).setTarget(2L);
        relationRepository.saveBySourceAndTarget(relationPO);
        NodePO nodePO = new NodePO();
        nodePO.setNodeId(1L).setProjectId(TEST_PROJECT_ID);
        nodeRepository.cascadeDeleteNode(nodePO);
        List<NodePO> nodePOList = nodeRepository.findByProjectId(TEST_PROJECT_ID);
        boolean res= true;
        for(NodePO nodePO1:nodePOList){
            if (nodePO1.getNodeId()==1){
                res = false;
                break;
            }
        }
        List<RelationPO> relationPOS = relationRepository.findByProjectId(TEST_PROJECT_ID);
        for(RelationPO relationPO1:relationPOS){
            if(relationPO1.getRelationId().equals(relationPO.getRelationId())){
                res= false;
                break;
            }
        }
        assert res;
    }

    @Test
    public void testSearchNodeByNameAndProjectId(){
        List<NodePO> nodes = nodeRepository.searchNodeByNameAndProjectId("皮卡丘",TEST_PROJECT_ID);
        assert nodes.size()>0;
        for(NodePO nodePO:nodes){
            System.out.println(nodePO);
        }
        System.out.println(nodes);
    }

    @Test
    public void testFindByNameFuzzy(){
        List<NodePO> nodes = nodeRepository.findByNameFuzzy(".*皮.*");
        assert nodes.size()>0;

        for(NodePO nodePO:nodes){
            System.out.println(nodePO);
        }
        System.out.println(nodes);
    }

    @Test
    public void testGetMaxNodeId(){
        int res = nodeRepository.getMaxNodeId(TEST_PROJECT_ID);
        System.out.println(res);
        assert res>0;
    }
}
