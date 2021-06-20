package com.cosin.infrastructure.security;

import com.cosin.dao.mysql.ProjectMapper;
import com.cosin.dao.mysql.UserMapper;
import com.cosin.model.dto.ProjectDTO;
import com.cosin.model.po.ProjectPO;
import com.cosin.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 用于权限验证类
 * projectService 加了缓存，所以实际上很快
 */
@Component
public class SecurityChecker {
    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectService projectService;

    public boolean checkByProjectId(int projectId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        ProjectDTO projectDTO = projectService.getProjectById(projectId);
        return projectDTO.getUserId()==userMapper.getUserByUsername(userDetails.getUsername()).getId();
    }

    public boolean checkByUserId(int userId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        return userId==userMapper.getUserByUsername(userDetails.getUsername()).getId();
    }
}
