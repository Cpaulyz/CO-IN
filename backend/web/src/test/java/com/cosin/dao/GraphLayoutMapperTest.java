package com.cosin.dao;

import com.cosin.COINWebApplication;
import com.cosin.dao.mysql.GraphLayoutMapper;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.po.GraphLayoutPO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class GraphLayoutMapperTest {
    @Autowired
    GraphLayoutMapper graphLayoutMapper;

    private static final int TEST_PROJECT_ID = 0;


    @Test
    public void testInsert(){
        GraphLayoutPO graphLayoutPO = new GraphLayoutPO().setProjectId(TEST_PROJECT_ID).setWidth(10.5).setHeight(999.9).setType(LayoutTypeEnum.FORCE);
        int res = graphLayoutMapper.insertOrUpdateGraphLayout(graphLayoutPO);
        assert res==1;
    }

    @Test
    public void testUpdate(){
        GraphLayoutPO graphLayoutPO = new GraphLayoutPO().setProjectId(TEST_PROJECT_ID).setWidth(10.5).setHeight(999.9).setType(LayoutTypeEnum.FORCE);
        int res = graphLayoutMapper.insertOrUpdateGraphLayout(graphLayoutPO);
        assert res==1;
        graphLayoutPO = new GraphLayoutPO().setProjectId(TEST_PROJECT_ID).setWidth(105.5).setHeight(9999.9).setType(LayoutTypeEnum.FORCE);
        res = graphLayoutMapper.insertOrUpdateGraphLayout(graphLayoutPO);
        assert res==2;
    }

    @Test
    public void testGetGraphLayoutByProjectIdAndType(){
        // 不存在
        GraphLayoutPO graphLayoutPO = graphLayoutMapper.getGraphLayoutByProjectIdAndType(TEST_PROJECT_ID,LayoutTypeEnum.FORCE);
        assert graphLayoutPO==null;
        // 插入后，再次测试
        graphLayoutMapper.insertOrUpdateGraphLayout(new GraphLayoutPO().setProjectId(TEST_PROJECT_ID).setWidth(10.5).setHeight(999.9).setType(LayoutTypeEnum.FORCE));
        graphLayoutPO = graphLayoutMapper.getGraphLayoutByProjectIdAndType(TEST_PROJECT_ID,LayoutTypeEnum.FORCE);
        assert graphLayoutPO!=null;
        assert graphLayoutPO.getProjectId()==TEST_PROJECT_ID;
        assert graphLayoutPO.getType()==LayoutTypeEnum.FORCE;
    }
}
