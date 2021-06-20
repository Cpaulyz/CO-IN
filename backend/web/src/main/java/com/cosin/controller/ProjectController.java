package com.cosin.controller;

import com.cosin.infrastructure.security.SecurityChecker;
import com.cosin.model.dto.ProjectDTO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.project.*;
import com.cosin.service.ProjectService;
import com.cosin.util.ResponseUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author: Chenyz
 * @Description: 知识图谱项目接口
 * 该接口下应该只关于项目操作，节点应该放在GraphController中
 */
@Api(tags = "知识图谱项目操作")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    private static final String XML_FORMAT_ERROR = "XML格式错误";
    private static final String XML_PROPERTY_ERROR = "XML属性错误";
    private static final String UNKOWN_ERROR = "未知错误";
    private static final String UPDATE_SUCCESS = "更新成功";
    private static final String UPDATE_FAILURE = "更新失败";

    @ApiOperation("创建知识图谱")
    @ApiResponses({
            @ApiResponse(code = 200,message = "成功",response = ProjectDetailVO.class),
            @ApiResponse(code = 500,message = "错误·",response = ResponseVO.class)
    })
    @PostMapping("/create")
    public ResponseEntity createProjectWithGraph(@RequestBody ProjectCreateVO projectCreateVO) {
        try {
            ProjectDTO projectDTO = projectService.createProject(new ProjectDTO(projectCreateVO));
            return ResponseUtils.success(new ProjectDetailVO(projectDTO));
        } catch (JsonParseException e) {
            System.out.println(XML_FORMAT_ERROR);
            return ResponseUtils.failure(XML_FORMAT_ERROR);
        } catch (JsonMappingException e) {
            System.out.println(XML_PROPERTY_ERROR);
            return ResponseUtils.failure(XML_PROPERTY_ERROR);
        } catch (Exception e) {
            if(e.getMessage()==null||"".equals(e.getMessage())){
                return ResponseUtils.failure(UNKOWN_ERROR);
            }else{
                return ResponseUtils.failure(e.getMessage());
            }
        }
    }

    @ApiOperation("导出知识图谱")
    @GetMapping("/export/{projectId}")
    @PreAuthorize("@securityChecker.checkByProjectId(#projectId)")
    public ResponseEntity<ProjectDetailVO> exportGraph(@PathVariable("projectId") int projectId) {
        try {
            ProjectDTO projectDTO = projectService.exportGraphByProjectId(projectId);
            return ResponseUtils.success(new ProjectDetailVO(projectDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseUtils.create(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("更新知识图谱项目名称")
    @PostMapping("/updateProjectName")
    @PreAuthorize("@securityChecker.checkByProjectId(#projectNameVO.projectId)")
    public ResponseEntity<ResponseVO> updateProjectName(@RequestBody ProjectNameVO projectNameVO) {
        boolean res = projectService.updateProjectName(projectNameVO.getProjectId(),projectNameVO.getName());
        if(res){
            return ResponseUtils.success(UPDATE_SUCCESS);
        }else{
            return ResponseUtils.failure(UPDATE_FAILURE);
        }
    }

    @ApiOperation("更新知识图谱项目描述信息")
    @PostMapping("/updateProjectDescription")
    @PreAuthorize("@securityChecker.checkByProjectId(#projectDescriptionVO.projectId)")
    public ResponseEntity<ResponseVO> updateProjectDescription(@RequestBody ProjectDescriptionVO projectDescriptionVO) {
        boolean res = projectService.updateProjectDescription(projectDescriptionVO.getProjectId(),projectDescriptionVO.getDescription());
        if(res){
            return ResponseUtils.success(UPDATE_SUCCESS);
        }else{
            return ResponseUtils.failure(UPDATE_FAILURE);
        }
    }

    @ApiOperation("更新项目默认布局")
    @PostMapping("/updateLayout")
    @PreAuthorize("@securityChecker.checkByProjectId(#projectLayoutVO.projectId)")
    public ResponseEntity<ResponseVO> updateProjectLayout(@RequestBody ProjectLayoutVO projectLayoutVO){
        boolean res = projectService.updateProjectLayout(projectLayoutVO.getProjectId(),projectLayoutVO.getLayoutStatus());
        if(res){
            return ResponseUtils.success(UPDATE_SUCCESS);
        }else{
            return ResponseUtils.failure(UPDATE_FAILURE);
        }
    }

    @ApiOperation("根据用户搜索知识图谱项目列表")
    @GetMapping("/listByUserId/{userId}")
    @PreAuthorize("@securityChecker.checkByUserId(#userId)")
    public ResponseEntity<List<ProjectDetailVO>> listProjectsByUserId(@PathVariable("userId") int userId) {
        List<ProjectDTO> projectDTOS = projectService.listProjectByUserId(userId,1);
        List<ProjectDetailVO> projectDetailVOS =  projectDTOS.stream().map(ProjectDetailVO::new).collect(Collectors.toList());
        return ResponseUtils.success(projectDetailVOS);
    }

    @ApiOperation("分页根据用户搜索知识图谱项目列表,currPage从1开始")
    @GetMapping("/listByUserId/{userId}/{currPage}")
    @PreAuthorize("@securityChecker.checkByUserId(#userId)")
    public ResponseEntity<List<ProjectDetailVO>> listProjectsByUserId(@PathVariable("userId") int userId,@PathVariable("currPage") int currPage) {
        List<ProjectDTO> projectDTOS = projectService.listProjectByUserId(userId,currPage<0?0:currPage);
        List<ProjectDetailVO> projectDetailVOS =  projectDTOS.stream().map(ProjectDetailVO::new).collect(Collectors.toList());
        return ResponseUtils.success(projectDetailVOS);
    }

    @ApiOperation("根据用户计算知识图谱项目数量")
    @GetMapping("/countByUserId/{userId}")
    @PreAuthorize("@securityChecker.checkByUserId(#userId)")
    public ResponseEntity<Integer> countByUserId(@PathVariable("userId") int userId) {
        return ResponseUtils.success(projectService.countProjectByUserId(userId));
    }

    @ApiOperation("获取某个project的详细描述")
    @GetMapping("/{projectId}")
//    @PreAuthorize("@securityChecker.checkByProjectId(#projectId)")
    public ResponseEntity<ProjectDetailVO> getProjectById(@PathVariable("projectId") int projectId) {
        ProjectDTO projectDTO = projectService.getProjectById(projectId);
        return ResponseUtils.success(new ProjectDetailVO(projectDTO));
    }

    @ApiOperation("更新项目状态(public/private)")
    @PostMapping("/updateProjectStatus")
    @PreAuthorize("@securityChecker.checkByProjectId(#projectStatusVO.projectId)")
    public ResponseEntity<ResponseVO> updateProjectStatus(@RequestBody ProjectStatusVO projectStatusVO) {
        boolean res = projectService.updateProjectStatus(projectStatusVO.getProjectId(),projectStatusVO.getStatus());
        if(res){
            return ResponseUtils.success(UPDATE_SUCCESS);
        }else{
            return ResponseUtils.failure(UPDATE_FAILURE);
        }
    }

    @ApiOperation("分页获取共有项目列表,currPage从1开始")
    @GetMapping("/publicProject/{currPage}")
    public ResponseEntity<List<ProjectDetailVO>> listPublicProject(@PathVariable("currPage") int currPage) {
        List<ProjectDTO> projectDTOS = projectService.listPublicProjects(currPage<0?0:currPage);
        List<ProjectDetailVO> projectDetailVOS =  projectDTOS.stream().map(ProjectDetailVO::new).collect(Collectors.toList());
        return ResponseUtils.success(projectDetailVOS);
    }

    @ApiOperation("分页获取共有项目数")
    @GetMapping("/countPublicProject")
    public ResponseEntity<Integer> countPublicProject() {
       return ResponseUtils.success(projectService.countPublicProject());
    }
}
