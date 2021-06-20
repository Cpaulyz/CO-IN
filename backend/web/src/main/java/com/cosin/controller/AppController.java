package com.cosin.controller;

import com.cosin.model.vo.GraphVO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.EntityQueryVO;
import com.cosin.model.vo.app.RankItemVO;
import com.cosin.model.vo.app.RankVO;
import com.cosin.model.vo.node.NodeRelNodeVO;
import com.cosin.model.vo.qa.GraphAnswerVO;
import com.cosin.model.vo.qa.QuestionVO;
import com.cosin.service.GraphService;
import com.cosin.service.QAService;
import com.cosin.util.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: Chenyz
 * @Description: 智能应用接口
 * 使用python-flask搭建的微服务，使用restTemplate进行请求
 */
@Api(tags = "知识图谱智能应用")
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    GraphService graphService;
    @Autowired
    QAService qaService;

    private static final String UNKNOWN_ERROR = "未知错误";

    @PostMapping("/question")
    @ApiOperation("图谱问答")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = GraphAnswerVO.class),
            @ApiResponse(code = 500, message = "错误·", response = ResponseVO.class)
    })
    public ResponseEntity question(@RequestBody QuestionVO questionVO) {
        GraphAnswerVO graphAnswerVO = qaService.question(questionVO);
        if (graphAnswerVO == null) {
            return ResponseUtils.failure(UNKNOWN_ERROR);
        } else {
            return ResponseUtils.success(graphAnswerVO);
        }
    }

    @GetMapping("/qaInitial/{projectId}")
    @ApiOperation("初始化图谱")
    public ResponseEntity<ResponseVO> qaInitial(@PathVariable("projectId") int projectId) {
        ResponseVO responseVO = qaService.qaInitial(projectId);
        if (responseVO.isSuccess()) {
            return ResponseUtils.success(responseVO);
        } else {
            return ResponseUtils.failure(responseVO);
        }
    }

    @GetMapping("/verifyInitial/{projectId}")
    @ApiOperation("验证图谱是否初始化")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = Boolean.class),
            @ApiResponse(code = 500, message = "错误·", response = ResponseVO.class)
    })
    public ResponseEntity<Boolean> verifyInitial(@PathVariable("projectId") int projectId) {
        boolean res = qaService.verifyInitial(projectId);
        return ResponseUtils.success(res);
    }


    @ApiOperation("实体查询")
    @PostMapping("/entityQuery")
    public ResponseEntity<GraphVO> entityQuery(@RequestBody EntityQueryVO entityQueryVO) {
        GraphVO graphVO = graphService.entityQuery(entityQueryVO);
        return ResponseUtils.success(graphVO);
    }

    @ApiOperation("关系查询")
    @PostMapping("/relQuery")
    public ResponseEntity<GraphVO> relQuery(@RequestBody NodeRelNodeVO nodeRelNodeVO) {
        GraphVO graphVO = graphService.relQuery(nodeRelNodeVO);
        return ResponseUtils.success(graphVO);
    }

    @ApiOperation("可能的relName")
    @GetMapping("relationNames/{projectId}")
    public ResponseEntity<List<String>> relationNames(@PathVariable("projectId") int projectId) {
        return ResponseUtils.success(graphService.findRelationNameByProjectId(projectId));
    }

    @ApiOperation("热门问答")
    @GetMapping("hotQuestion/{projectId}")
    public ResponseEntity<List<String>> hotQuestion(@PathVariable("projectId") int projectId) {
        return ResponseUtils.success(qaService.hotQuestion(projectId));
    }

    @ApiOperation("中心识别")
    @GetMapping("centrality/{projectId}")
    public ResponseEntity<List<RankVO>> centrality(@PathVariable("projectId") int projectId) {
        List<RankVO> rankVOS = qaService.centrality(projectId);
        return ResponseUtils.success(rankVOS);
    }

}
