package com.cosin.dao;

import com.cosin.COINWebApplication;
import com.cosin.dao.mysql.LayoutMapper;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.po.NodeLayoutPO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class NodeLayoutMapperTest {
    @Autowired
    LayoutMapper layoutMapper;

    private static final int TEST_PROJECT_ID = 0;

    private NodeLayoutPO nodeLayoutPO;

    @Before
    public void setup(){
        nodeLayoutPO = new NodeLayoutPO().setNodeId(1).setProjectId(TEST_PROJECT_ID).setType(LayoutTypeEnum.FORCE).setXAxis(8.8).setYAxis(9.5);
        layoutMapper.insertOrUpdateLayout(nodeLayoutPO);
        System.out.println("============setup==============");
        System.out.println(nodeLayoutPO);
    }

    @Test
    public void testInsert(){
        NodeLayoutPO nodeLayoutPO1 = new NodeLayoutPO().setNodeId(2).setProjectId(TEST_PROJECT_ID).setType(LayoutTypeEnum.FORCE).setXAxis(8.8).setYAxis(9.5);
        int res = layoutMapper.insertOrUpdateLayout(nodeLayoutPO1);
        assert res==1;
    }

    @Test
    public void testUpdate(){
        NodeLayoutPO nodeLayoutPO1 = new NodeLayoutPO().setNodeId(1).setProjectId(TEST_PROJECT_ID).setType(LayoutTypeEnum.FORCE).setXAxis(1.1).setYAxis(1.1);
        int res = layoutMapper.insertOrUpdateLayout(nodeLayoutPO1);
        assert res==2;
    }


    @Test
    public void testListLayoutByProjectId(){
        List<NodeLayoutPO> nodeLayoutPOS = layoutMapper.listLayoutByProjectId(TEST_PROJECT_ID);
        System.out.println("list->"+nodeLayoutPOS);
        assert nodeLayoutPOS.size()==1;
    }

    @Test
    public void testListLayoutByProjectIdAndType(){
        List<NodeLayoutPO> nodeLayoutPOS =layoutMapper.listLayoutByProjectIdAndType(TEST_PROJECT_ID,LayoutTypeEnum.FORCE);
        System.out.println("list->"+nodeLayoutPOS);
        assert nodeLayoutPOS.size()==1;
    }

    @Test
    public void testDeleteLayoutByNodeIdAndProjectId(){
        int res = layoutMapper.deleteLayoutByNodeIdAndProjectId(nodeLayoutPO.getNodeId(),TEST_PROJECT_ID);
        List<NodeLayoutPO> nodeLayoutPOS = layoutMapper.listLayoutByProjectId(TEST_PROJECT_ID);
        System.out.println("list->"+nodeLayoutPOS);
        assert nodeLayoutPOS.size()==0;
        assert res>0;
    }


    @Test
    public void testDeleteLayoutByProjectId(){
        NodeLayoutPO nodeLayoutPO1 = new NodeLayoutPO().setNodeId(2).setProjectId(TEST_PROJECT_ID).setType(LayoutTypeEnum.FORCE).setXAxis(8.8).setYAxis(9.5);
        layoutMapper.insertOrUpdateLayout(nodeLayoutPO1);
        int res = layoutMapper.deleteLayoutByProjectId(TEST_PROJECT_ID);
        List<NodeLayoutPO> nodeLayoutPOS = layoutMapper.listLayoutByProjectId(TEST_PROJECT_ID);
        System.out.println("list->"+nodeLayoutPOS);
        assert nodeLayoutPOS.size()==0;
        assert res>0;
    }

    @Test
    public void testListLayoutByProjectIdAndNodeIdsAndType(){
        List<Long> nodes = new ArrayList<>();
        nodes.add(1L);
        List<NodeLayoutPO> nodeLayoutPOS = layoutMapper.listLayoutByProjectIdAndNodeIdsAndType(TEST_PROJECT_ID,nodes,LayoutTypeEnum.FORCE);
        assert nodeLayoutPOS.size()==1;
        System.out.println(nodeLayoutPOS);
    }

}
