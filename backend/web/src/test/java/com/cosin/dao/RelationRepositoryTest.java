package com.cosin.dao;


import com.cosin.COINWebApplication;
import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.neo4j.RelationRepository;
import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.model.po.NodePO;
import com.cosin.model.po.RelationPO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RelationRepositoryTest {
    @Autowired
    RelationRepository relationRepository;
    @Autowired
    NodeRepository nodeRepository;
    @Autowired
    GraphIdRepository graphIdRepository;

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
        NodePO node1 =nodeRepository.save(nodePO1);
        NodePO node2 =nodeRepository.save(nodePO2);

        RelationPO relationPO = new RelationPO()
                .setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setName("主人")
                .setSource(node2.getNodeId()).setTarget(node1.getNodeId()).setProjectId(TEST_PROJECT_ID);

        RelationPO relationPO1 = relationRepository.saveBySourceAndTarget(relationPO);
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(relationPO1);
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
    public void testInsert(){
        RelationPO relationPO = new RelationPO()
                .setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setName("插入测试")
                .setSource(1L).setTarget(2L).setProjectId(TEST_PROJECT_ID);

        RelationPO relationPO1 = relationRepository.saveBySourceAndTarget(relationPO);
        System.out.println(relationPO1);
        assert relationPO1.getName().equals(relationPO.getName());
        assert relationPO1.getSourceNode().getNodeId()==relationPO.getSource();
        assert relationPO1.getTargetNode().getNodeId()==relationPO.getTarget();
    }

    @Test
    public void testSave(){
        RelationPO relationPO = new RelationPO()
                .setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setName("插入测试")
                .setSource(1L).setTarget(2L).setProjectId(TEST_PROJECT_ID);
        relationPO.setSourceNode(nodeRepository.findByNodeIdAndProjectId(relationPO.getSource(),relationPO.getProjectId()));
        relationPO.setTargetNode(nodeRepository.findByNodeIdAndProjectId(relationPO.getTarget(),relationPO.getProjectId()));
        RelationPO relationPO1 = relationRepository.save(relationPO);
        System.out.println("save->"+relationPO1);
        assert relationPO1.getName().equals(relationPO.getName());
        assert relationPO1.getSourceNode().getNodeId()==relationPO.getSource();
        assert relationPO1.getTargetNode().getNodeId()==relationPO.getTarget();
    }

    @Test
    public void testSelectByProjectId(){
        List<RelationPO> relationPOS = relationRepository.findByProjectId(TEST_PROJECT_ID);
        for (RelationPO relationPO:relationPOS){
            assert relationPO.getProjectId()==TEST_PROJECT_ID;
        }
    }

    @Test
    public void testUpdate(){
        RelationPO relationPO = new RelationPO()
                .setRelationId(1L).setName("修改").setProjectId(TEST_PROJECT_ID);
        RelationPO relationPO1 = relationRepository.updateByRel(relationPO);
        System.out.println(relationPO1);
        assert relationPO1.getName().equals(relationPO.getName());
    }

    @Test
    public void testUpdateBySave(){
        RelationPO relationPO = new RelationPO()
                .setRelationId(1L).setName("修改")
                .setSource(1L).setTarget(2L).setProjectId(TEST_PROJECT_ID);
        relationPO.setSourceNode(nodeRepository.findByNodeIdAndProjectId(relationPO.getSource(),relationPO.getProjectId()));
        relationPO.setTargetNode(nodeRepository.findByNodeIdAndProjectId(relationPO.getTarget(),relationPO.getProjectId()));
        relationPO.setId(relationRepository.findIdByRelationIdAndProjectId(relationPO.getRelationId(),relationPO.getProjectId()));
        RelationPO relationPO1 = relationRepository.save(relationPO);
        System.out.println("after update->"+relationPO1);
        assert relationPO1.getName().equals(relationPO.getName());
    }

    @Test
    public void testDelete(){
        RelationPO relationPO = new RelationPO()
                .setRelationId(1L).setName("修改").setProjectId(TEST_PROJECT_ID);
        relationRepository.deleteByRelation(relationPO);
        boolean res =true;
        List<RelationPO> relationPOS = relationRepository.findByProjectId(TEST_PROJECT_ID);
        for (RelationPO relationPO1:relationPOS){
            if(relationPO1.getRelationId()==1L){
                res =false;
                break;
            }
        }
        assert res;
    }

    @Test
    public void testListBySourceOrTarget(){
        NodePO nodePO1 = new NodePO().setNodeId(1L).setProjectId(TEST_PROJECT_ID);
        List<RelationPO> relationPOS = relationRepository.listBySourceOrTarget(nodePO1);
        System.out.println(relationPOS);
        assert relationPOS.size()==1;
    }

    @Test
    public void testFindIdByRelationIdAndProjectId(){
        long res = relationRepository.findIdByRelationIdAndProjectId(1L,TEST_PROJECT_ID);
        System.out.println(res);
        assert res!=0;
    }

    @Test
    public void testFindBySourceName(){
        NodePO nodePO3 = new NodePO().setNodeId(graphIdRepository.generateNodeIdByProjectId(TEST_PROJECT_ID)).setName("33").setGroup("人物")
                .setRadius(3).setColor("color").setFigure("figure").setTextSize("12").setProjectId(TEST_PROJECT_ID);
        NodePO node3 =nodeRepository.save(nodePO3);
        System.out.println(node3);
        RelationPO relationPO = new RelationPO()
                .setRelationId(graphIdRepository.generateRelationIdByProjectId(TEST_PROJECT_ID)).setName("主人")
                .setSource(1L).setTarget(node3.getNodeId()).setProjectId(TEST_PROJECT_ID);
        RelationPO relationPO1 = relationRepository.saveBySourceAndTarget(relationPO);
        System.out.println(relationPO1);
        System.out.println("find...");
        // check 正常查询
        List<RelationPO> relationPOS = relationRepository.findBySourceName(TEST_PROJECT_ID,"皮卡丘");
        assert relationPOS.size()>1;
        for (RelationPO r:relationPOS) {
            System.out.println(r);
        }
        // check 模糊查询
        relationPOS = relationRepository.findBySourceName(TEST_PROJECT_ID,".*卡.*");
        assert relationPOS.size()>1;
        for (RelationPO r:relationPOS) {
            System.out.println(r);
        }
    }

    @Test
    public void testFindByProjectIdAndNameAndTargetAndSourceFuzzy(){
        // 情况1. n-r->m 都有
        List<RelationPO> relationPOS = relationRepository.findByProjectIdAndNameAndTargetAndSourceFuzzy(TEST_PROJECT_ID,"主人","小智","皮卡丘");
        assert relationPOS.size()==1;
        for (RelationPO r:relationPOS) {
            System.out.println(r);
        }
        // 情况2. n-r->?
        relationPOS = relationRepository.findByProjectIdAndNameAndTargetAndSourceFuzzy(TEST_PROJECT_ID,"主人","小智",".*");
        assert relationPOS.size()==1;
        for (RelationPO r:relationPOS) {
            System.out.println(r);
        }
        // 情况3. ?-r->m
        relationPOS = relationRepository.findByProjectIdAndNameAndTargetAndSourceFuzzy(TEST_PROJECT_ID,"主人",".*","皮卡丘");
        assert relationPOS.size()==1;
        for (RelationPO r:relationPOS) {
            System.out.println(r);
        }
        // 情况4. ?-r->?
        relationPOS = relationRepository.findByProjectIdAndNameAndTargetAndSourceFuzzy(TEST_PROJECT_ID,"主人",".*",".*");
        assert relationPOS.size()==1;
        for (RelationPO r:relationPOS) {
            System.out.println(r);
        }
    }

    @Test
    public void testFindRelationNameByProjectId(){
        List<String> res = relationRepository.findRelationNameByProjectId(TEST_PROJECT_ID);
        assert res.size()>0;
        System.out.println(res);
    }

    @Test
    public void testFindByProjectIdAndTargetIdAndSourceId(){
        RelationPO relationPOS = relationRepository.findByProjectIdAndTargetIdAndSourceId(TEST_PROJECT_ID,2,1);
        assert relationPOS != null;
    }

    @Test
    public void testGetMaxRelationId(){
        int res = relationRepository.getMaxRelationId(TEST_PROJECT_ID);
        System.out.println(res);
        assert res>0;
    }
}
