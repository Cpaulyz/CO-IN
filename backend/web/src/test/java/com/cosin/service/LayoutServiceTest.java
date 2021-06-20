package com.cosin.service;

import com.cosin.COINWebApplication;
import com.cosin.model.dto.LayoutDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.vo.node.NodeLayoutVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class LayoutServiceTest {
    @Autowired
    LayoutService layoutService;

    private static final int TEST_PROJECT_ID = 0;

    @Before
    public void setUp(){
        LayoutDTO layoutDTO = new LayoutDTO();
        layoutDTO.setProjectId(TEST_PROJECT_ID);
        layoutDTO.setType(LayoutTypeEnum.FORCE);
        layoutDTO.getNodes().add(new NodeLayoutVO(1,1.5,1.5));
        layoutDTO.getNodes().add(new NodeLayoutVO(2,1.5,1.5));
        layoutService.saveLayout(layoutDTO);
    }

    @Test
    public void testGetLayoutByProjectId(){
        List<LayoutDTO> layoutDTOS = layoutService.getLayoutByProjectId(TEST_PROJECT_ID);
        assert layoutDTOS.size()==3;
    }

    @Test
    public void testSaveLayout(){
        LayoutDTO layoutDTO = new LayoutDTO();
        layoutDTO.setProjectId(TEST_PROJECT_ID);
        layoutDTO.setType(LayoutTypeEnum.FIXED);
        layoutDTO.getNodes().add(new NodeLayoutVO(1,1.5,1.5));
        layoutDTO.getNodes().add(new NodeLayoutVO(2,1.5,1.5));
        layoutService.saveLayout(layoutDTO);
        List<LayoutDTO> layoutDTOS = layoutService.getLayoutByProjectId(TEST_PROJECT_ID);
        for(LayoutDTO layoutDTO1:layoutDTOS){
            assert layoutDTO1.getType() != LayoutTypeEnum.FIXED || layoutDTO1.getNodes().size() != 0;
        }
    }

    @Test
    public void testDeleteLayoutByProjectId(){
         assert layoutService.deleteLayoutByProjectId(TEST_PROJECT_ID).isSuccess();
    }

    @Test
    public void testDeleteLayoutByNodeIdAndProjectId(){
         assert layoutService.deleteLayoutByNodeIdAndProjectId(1,TEST_PROJECT_ID).isSuccess();
    }

}
