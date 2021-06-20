package com.cosin.controller;

import com.cosin.model.dto.*;
import com.cosin.model.vo.GraphVO;
import com.cosin.model.vo.LayoutVO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.node.NodeIdVO;
import com.cosin.model.vo.node.NodeVO;
import com.cosin.model.vo.qa.HelperQuestionVO;
import com.cosin.model.vo.relation.RelationIdVO;
import com.cosin.model.vo.relation.RelationVO;
import com.cosin.service.GraphService;
import com.cosin.service.LayoutService;
import com.cosin.util.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Chenyz
 * @Description: 知识图谱节点操作接口
 * 该接口下应该只关于节点操作，项目操作应该放在ProjectController中
 */
@Api(tags = "知识图谱节点操作")
@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    GraphService graphService;
    @Autowired
    LayoutService layoutService;

    @ApiOperation("根据id查找知识图谱")
    @GetMapping("/{projectId}")
    @PreAuthorize("@securityChecker.checkByProjectId(#projectId)")
    public ResponseEntity<GraphVO> getGraphByProjectId(@PathVariable("projectId") int projectId) {
        GraphVO graphVO = graphService.getGraphByProjectId(projectId);
        return ResponseUtils.create(graphVO, HttpStatus.OK);
    }

    @ApiOperation("添加实体，添加到projectId下")
    @PostMapping("/insertNode")
    @PreAuthorize("@securityChecker.checkByProjectId(#nodeVO.projectId)")
    public ResponseEntity<NodeIdVO> insertNode(@RequestBody NodeVO nodeVO) {
        NodeDTO res = graphService.insertNode(new NodeDTO(nodeVO));
        return ResponseUtils.create(new NodeIdVO(res.getNodeId(),res.getProjectId()), HttpStatus.OK);
    }

    @ApiOperation("添加关系，添加到projectId下，要携带由target指向source")
    @PostMapping("/insertRel")
    @ApiResponses({
            @ApiResponse(code = 200,message = "成功",response = RelationIdVO.class),
            @ApiResponse(code = 500,message = "错误·",response = ResponseVO.class)
    })
    @PreAuthorize("@securityChecker.checkByProjectId(#relationVO.projectId)")
    public ResponseEntity insertRel(@RequestBody RelationVO relationVO) {
        RelationDTO res  = graphService.insertRelation(new RelationDTO(relationVO));
        if(res!=null){
            return ResponseUtils.create(new RelationIdVO(res.getRelationId(),res.getProjectId()), HttpStatus.OK);
        }else{
            return ResponseUtils.failure("插入失败，可能是关系相关节点已不存在");
        }
    }

    @ApiOperation("更新实体，实体和关系要带正确id")
    @PostMapping("/updateNode")
    @PreAuthorize("@securityChecker.checkByProjectId(#nodeVO.projectId)")
    public ResponseEntity<ResponseVO> updateNode(@RequestBody NodeVO nodeVO) {
        NodeDTO res = graphService.updateNode(new NodeDTO(nodeVO));
        if(res!=null){
            return ResponseUtils.success(ResponseVO.buildSuccess());
        }else{
            return ResponseUtils.failure("更新失败");
        }
    }

    @ApiOperation("更新布局信息")
    @PostMapping("/updateLayout")
    @PreAuthorize("@securityChecker.checkByProjectId(#layoutVO.projectId)")
    public ResponseEntity<ResponseVO> updateLayout(@RequestBody LayoutVO layoutVO) {
        System.out.println(layoutVO);
        try{
            ResponseVO res = layoutService.saveLayout(new LayoutDTO(layoutVO));
            return ResponseUtils.success(res);
        }catch (Exception e){
            return ResponseUtils.failure("更新失败");
        }
    }

    @ApiOperation("更新关系，实体和关系要带正确id")
    @PostMapping("/updateRel")
    @PreAuthorize("@securityChecker.checkByProjectId(#relationVO.projectId)")
    public ResponseEntity<ResponseVO> updateRel(@RequestBody RelationVO relationVO) {
        RelationDTO res  = graphService.updateRelation(new RelationDTO(relationVO));
        if(res!=null){
            return ResponseUtils.success();
        }else{
            return ResponseUtils.failure("更新失败");
        }
    }

    @ApiOperation("删除实体，如果带有关系，会报错，要带正确id")
    @ApiResponses({
            @ApiResponse(code = 200,message = "成功",response = ResponseVO.class),
            @ApiResponse(code = 500,message = "错误·",response = ResponseVO.class)
    })
    @PostMapping("/deleteNode")
    @PreAuthorize("@securityChecker.checkByProjectId(#nodeIdVO.projectId)")
    public ResponseEntity<ResponseVO> deleteNode(@RequestBody NodeIdVO nodeIdVO) {
        ResponseVO responseVO = graphService.deleteNode(new NodeDTO(nodeIdVO));
        if(responseVO.isSuccess()){
            return ResponseUtils.success();
        }else{
            return ResponseUtils.failure(responseVO);
        }
    }

    @ApiOperation("级联删除实体，会删除NodeVO及其相关关系，要带正确id")
    @PostMapping("/cascadeDeleteNode")
    @PreAuthorize("@securityChecker.checkByProjectId(#nodeIdVO.projectId)")
    public ResponseEntity<ResponseVO> cascadeDeleteNode(@RequestBody NodeIdVO nodeIdVO) {
        graphService.cascadeDeleteNode(new NodeDTO(nodeIdVO));
        return ResponseUtils.success();
    }

    @ApiOperation("删除关系，要带正确id")
    @PostMapping("/deleteRel")
    @PreAuthorize("@securityChecker.checkByProjectId(#relationIdVO.projectId)")
    public ResponseEntity<ResponseVO> deleteRel(@RequestBody RelationIdVO relationIdVO) {
        graphService.deleteRelation(new RelationDTO(relationIdVO));
        return ResponseUtils.success();
    }
    @ApiOperation("查询相关的项目")
    @PostMapping("/projectQuery")
    public ResponseEntity<ResponseVO> projectQuery(@RequestBody HelperQuestionVO helperQuestionVO) {
        String res = graphService.projectQuery(helperQuestionVO.getText());
        return ResponseUtils.success(res);
    }
}
