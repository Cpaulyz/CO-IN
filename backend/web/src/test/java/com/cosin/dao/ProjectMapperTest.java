package com.cosin.dao;

import com.cosin.COINWebApplication;
import com.cosin.dao.mysql.ProjectMapper;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import com.cosin.model.po.ProjectPO;
import com.cosin.util.PageHelper;
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
public class ProjectMapperTest {
    @Autowired
    ProjectMapper projectMapper;

    private static int TEST_PROJECT_ID = 3;

    @Test
    public void testListByUserId(){
        int currIndex = PageHelper.getIndexByCurrPage(1);
        List<ProjectPO> projectPOS = projectMapper.listProjectsByUserId(1, currIndex,PageHelper.PAGE_SIZE);
        assert projectPOS.size()>0;
        System.out.println(projectPOS);
    }

    @Test
    public void testCountByUserId(){
        int res = projectMapper.countProjectsByUserId(1);
        assert res>0;
        System.out.println(res);
    }

    @Test
    public void testInsertProject(){
        ProjectPO projectPO = new ProjectPO().setName("测试插入").setDescription("插入").setUserId(0).setLayoutStatus(LayoutTypeEnum.FORCE).setStatus(ProjectStatusEnum.PRIVATE);
        int res = projectMapper.insertProject(projectPO);
        assert res>0;
    }

    @Test
    public void testGetByProjectId(){
        ProjectPO projectPO = projectMapper.getProjectByProjectId(TEST_PROJECT_ID);
        assert projectPO!=null;
    }

    @Test
    public void testUpdateProjectName(){
        String newName = "修改";
        projectMapper.updateProjectName(TEST_PROJECT_ID,newName);
        ProjectPO projectPO1 = projectMapper.getProjectByProjectId(TEST_PROJECT_ID);
        System.out.println(projectPO1);
        assert projectPO1.getName().equals(newName);
    }

    @Test
    public void testUpdateProjectDescription(){
        String newDesc = "修改";
        projectMapper.updateProjectDescription(TEST_PROJECT_ID,newDesc);
        ProjectPO projectPO1 = projectMapper.getProjectByProjectId(TEST_PROJECT_ID);
        System.out.println(projectPO1);
        assert projectPO1.getDescription().equals(newDesc);
    }

    @Test
    public void testUpdateProjectLayout(){
        int res =  projectMapper.updateProjectLayout(TEST_PROJECT_ID,LayoutTypeEnum.FIXED);
        assert res>0;
        ProjectPO projectPO1 = projectMapper.getProjectByProjectId(TEST_PROJECT_ID);
        assert projectPO1.getLayoutStatus()==LayoutTypeEnum.FIXED;
    }

    @Test
    public void testUpdateProjectStatus(){
        int res =  projectMapper.updateProjectStatus(TEST_PROJECT_ID, ProjectStatusEnum.PUBLIC);
        assert res>0;
        ProjectPO projectPO1 = projectMapper.getProjectByProjectId(TEST_PROJECT_ID);
        assert projectPO1.getStatus()==ProjectStatusEnum.PUBLIC;
    }

    @Test
    public void testListPublicProjects(){
        int currIndex = PageHelper.getIndexByCurrPage(1);
        List<ProjectPO> projectPOS = projectMapper.listPublicProjects(currIndex,PageHelper.PAGE_SIZE);
        assert projectPOS.size()>0;
        System.out.println(projectPOS);
    }

    @Test
    public void testCountPublicProject(){
        int res = projectMapper.countPublicProject();
        System.out.println(res);
        assert res>0;
    }
}
