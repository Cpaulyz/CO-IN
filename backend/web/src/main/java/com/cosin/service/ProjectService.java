package com.cosin.service;

import com.cosin.model.dto.ProjectDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {

    /**
     * 导出知识图谱，重点是xml
     * @param projectId
     * @return
     * @throws JsonProcessingException
     */
    ProjectDTO exportGraphByProjectId(int projectId) throws JsonProcessingException;

    /**
     * 更新项目默认布局
     */
    boolean updateProjectLayout(int projectId, LayoutTypeEnum layout);

    /**
     * 创建知识图谱，返回对象注入projectId
     * @param projectDTO
     * @return
     * @throws IOException
     */
    ProjectDTO createProject(ProjectDTO projectDTO) throws Exception;

    /**
     * 根据用户id列出projects
     * @param userId
     * @return
     */
    List<ProjectDTO> listProjectByUserId(int userId,int currPage);

    /**
     * 根据用户id列出项目数量
     */
    int countProjectByUserId(int userId);

    /**
     * 更新项目名称
     * @param projectId
     * @param name
     * @return
     */
    boolean updateProjectName(int projectId,String name);

    /**
     * 更新项目描述
     * @param projectId
     * @param description
     * @return
     */
    boolean updateProjectDescription(int projectId,String description);

    /**
     * 更新项目主图
     * @param projectId
     * @param image
     * @return
     */
    String updateProjectImage(int projectId, MultipartFile image);

    /**
     * 根据projectId获取知识图谱项目信息
     * @param projectId
     * @return
     */
    ProjectDTO getProjectById(int projectId);

    /**
     * 更新项目状态
     * @param projectId 项目id，projectStatusEnum 状态
     * @return 更新失败与否
     */
    boolean updateProjectStatus(int projectId, ProjectStatusEnum projectStatusEnum);

    /**
     * 获取共有项目列表
     * @param currPage 页数
     * @return
     */
    List<ProjectDTO> listPublicProjects(int currPage);


    /**
     * 获取共有项目数量
     * @return
     */
    int countPublicProject();

}
