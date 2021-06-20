package com.cosin.service;

import com.cosin.COINWebApplication;
import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.model.dto.ProjectDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class ProjectServiceTest {
    @Autowired
    ProjectService projectService;
    @Autowired
    GraphIdRepository graphIdRepository;
    private static int TEST_PROJECT_ID = 3;
    private static final int TEST_USER_ID =0;

    @Test
    public void testCreateProjectWithoutXML(){
        ProjectDTO projectDTO = new ProjectDTO().setName("测试").setDescription("我是描述").setUserId(TEST_USER_ID).setStatus(ProjectStatusEnum.PUBLIC);
        try {
            projectService.createProject(projectDTO);
            ProjectDTO projectDTO1 = projectService.getProjectById(projectDTO.getProjectId());
            System.out.println(projectDTO);
            assert projectDTO.getProjectId().equals(projectDTO1.getProjectId());
            assert projectDTO.getDescription().equals(projectDTO1.getDescription());
            assert projectDTO.getName().equals(projectDTO1.getName());
            assert projectDTO.getUserId().equals(projectDTO1.getUserId());
            assert projectDTO1.getLayoutStatus()== LayoutTypeEnum.FORCE;
            assert projectDTO1.getStatus()==ProjectStatusEnum.PUBLIC;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateProjectWithXML(){
        ProjectDTO projectDTO = new ProjectDTO().setName("测试").setDescription("我是描述").setUserId(TEST_USER_ID);
        projectDTO.setXml("<graph><nodes><node><nodeId>1</nodeId><name>皮卡丘</name><group>宠物</group><radius>1</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties><生日>12-20</生日><身高>120</身高></properties></node><node><nodeId>2</nodeId><name>杰尼龟</name><group>宠物</group><radius>2</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties/></node><node><nodeId>3</nodeId><name>小智</name><group>人物</group><radius>1</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties/></node><node><nodeId>4</nodeId><name>cyz</name><group>人物</group><radius>3</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties/></node></nodes><rels><rel><relationId>2</relationId><name>主人</name><source>3</source><target>1</target><width>1</width></rel><rel><relationId>1</relationId><name>主人</name><source>4</source><target>1</target><width>2</width></rel><rel><relationId>4</relationId><name>朋友</name><source>1</source><target>2</target><width>1</width></rel><rel><relationId>3</relationId><name>主人</name><source>3</source><target>2</target><width>1</width></rel></rels></graph>");
        try {
            ProjectDTO project = projectService.createProject(projectDTO);
            graphIdRepository.setRelationIdByProjectId(project.getProjectId(),0L);
            graphIdRepository.setNodeIdByProjectId(project.getProjectId(),0L);
            assert project!=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected = Exception.class)
    public void testCreateProjectWithXMLFail() throws Exception {
        ProjectDTO projectDTO = new ProjectDTO().setName("test").setDescription("description").setUserId(1);
        projectDTO.setXml("<graph><nodes><node><nodeId>1</nodeId><name>皮卡丘</name><group>宠物</group><radius>1</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties><生日>12-20</生日><身高>120</身高></properties></node><node><nodeId>2</nodeId><name>杰尼龟</name><group>宠物</group><radius>2</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties/></node><node><nodeId>3</nodeId><name>小智</name><group>人物</group><radius>1</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties/></node><node><nodeId>4</nodeId><name>cyz</name><group>人物</group><radius>3</radius><projectId>1</projectId><color>待补充</color><textSize>15</textSize><figure>circle</figure><properties/></node></nodes><rels><rel><relationId>2</relationId><name>主人</name><source>3</source><target>1</target><width>1</width></rel><rel><relationId>1</relationId><name>主人</name><source>4</source><target>1</target><width>2</width></rel><rel><relationId>4</relationId><name>朋友</name><source>1</source><target>2</target><width>1</width></rel><rel><relationId>3</relationId><name>主人</name><source>3</source><target>2</target><width>1</width></rel></rels></graphs>");
        ProjectDTO project = projectService.createProject(projectDTO);
        assert project!=null;
    }

    @Test
    public void testExport(){
        try {
            ProjectDTO projectDTO = projectService.exportGraphByProjectId(TEST_PROJECT_ID);
            System.out.println(projectDTO);
            assert projectDTO !=null;
            assert projectDTO.getXml()!=null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateLayout(){
        boolean res = projectService.updateProjectLayout(TEST_PROJECT_ID,LayoutTypeEnum.FIXED);
        assert res;
    }

    @Test
    public void testUpdateProjectNameAndDescription(){
        ProjectDTO projectDTO = new ProjectDTO().setName("测试").setDescription("我是描述").setUserId(TEST_USER_ID);
        try {
            projectService.createProject(projectDTO);
            projectDTO.setName("name更新").setDescription("desc更新");
            projectService.updateProjectName(projectDTO.getProjectId(),projectDTO.getName());
            projectService.updateProjectDescription(projectDTO.getProjectId(),projectDTO.getDescription());
            ProjectDTO projectDTO1 = projectService.getProjectById(projectDTO.getProjectId());
            System.out.println(projectDTO);
            assert projectDTO.getName().equals(projectDTO1.getName());
            assert projectDTO.getDescription().equals(projectDTO1.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListProjectsByUserId(){
        List<ProjectDTO> projectDTOList = projectService.listProjectByUserId(1,1);
        assert projectDTOList.size()!=0;
    }


    @Test
    public void testCountByUserId(){
        int res = projectService.countProjectByUserId(1);
        System.out.println(res);
        assert res>0;
    }


    @Test
    public void testGetProjectById(){
        ProjectDTO projectDTO = projectService.getProjectById(TEST_PROJECT_ID);
        assert projectDTO!=null;
    }

    @Test
    public void testUpdateProjectStatus(){
        boolean res = projectService.updateProjectStatus(TEST_PROJECT_ID, ProjectStatusEnum.PUBLIC);
        assert res;
    }

    @Test
    public void testListPublicProjects(){
        List<ProjectDTO> projectDTOS = projectService.listPublicProjects(1);
        assert projectDTOS.size()>0;
    }

    @Test
    public void testCountPublicProject(){
        int res = projectService.countPublicProject();
        System.out.println(res);
        assert res>0;
    }
}
