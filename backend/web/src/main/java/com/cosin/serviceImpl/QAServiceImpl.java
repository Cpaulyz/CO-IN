package com.cosin.serviceImpl;

import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.FlaskAnswerVO;
import com.cosin.model.vo.app.RankVO;
import com.cosin.model.vo.qa.GraphAnswerVO;
import com.cosin.model.vo.qa.QuestionVO;
import com.cosin.service.GraphService;
import com.cosin.service.QAService;
import com.cosin.util.HeartBeating;
import com.cosin.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author chenyanze
 * @description 知识图谱w问答相关
 */
@Service
public class QAServiceImpl implements QAService {

    @Value(("${pyserver.url}"))
    String SERVER_URL;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GraphService graphService;

    private static final String UNKNOWN_ERROR = "未知错误";

    /**
     * 转发服务
     * @param url URL
     * @param responseType 类型
     * @param <T> 类型
     * @return 如果服务失效，返回null，否则转发
     */
    private <T> ResponseEntity<T> get(String url,Class<T> responseType){
        if(!HeartBeating.alive){
            return ResponseUtils.success((T)null);
        }else{
            return  restTemplate.getForEntity(url, responseType);
        }
    }

    @Override
    public ResponseVO qaInitial(int projectId) {
        String url = SERVER_URL + "/question/init/" + projectId;
        try {

            ResponseEntity<FlaskAnswerVO> responseEntity = get(url, FlaskAnswerVO.class);
            if (responseEntity.getBody() == null) {
                return ResponseVO.buildFailure(UNKNOWN_ERROR);
            } else if (responseEntity.getBody().isSuccess()) {
                return ResponseVO.buildSuccess(responseEntity.getBody().getText());
            } else {
                return ResponseVO.buildFailure(responseEntity.getBody().getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure(e.getMessage());
        }
    }

    @Override
    public GraphAnswerVO question(QuestionVO questionVO) {
        String url = SERVER_URL + "/question/" + questionVO.getProjectId() + "/" + questionVO.getText();
        try {
            ResponseEntity<FlaskAnswerVO> responseEntity = get(url, FlaskAnswerVO.class);
            if (responseEntity.getBody() == null) {
                return null;
            } else if (responseEntity.getBody().isSuccess()) {
//                System.out.println(responseEntity.getBody());
                GraphAnswerVO answerVO = new GraphAnswerVO();
                answerVO.setText(responseEntity.getBody().getText());
                answerVO.setGraph(graphService.getGraphFromNodePair(questionVO.getProjectId(), responseEntity.getBody().getNodes()));
                return answerVO;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean verifyInitial(int projectId) {
        String url = SERVER_URL + "/question/verify/" + projectId;
        try {
            ResponseEntity<FlaskAnswerVO> responseEntity = get(url, FlaskAnswerVO.class);
            if (responseEntity.getBody() == null) {
                return false;
            } else {
                return responseEntity.getBody().isSuccess();
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> hotQuestion(int projectId) {
        String url = SERVER_URL + "/question/hot/" + projectId;
        List<String> res = new ArrayList<>();
        try {
            ResponseEntity<List> responseEntity = get(url, List.class);
            if (responseEntity.getBody() == null) {
                return res;
            } else {
                return (List<String>) responseEntity.getBody();
            }
        } catch (Exception e) {
            return res;
        }
    }

    @Override
    public List<RankVO> centrality(int projectId) {
        String url = SERVER_URL + "/centrality/" + projectId;
        List<RankVO> res = new ArrayList<>();
        try {
            ResponseEntity<List> responseEntity = get(url, List.class);
            if (responseEntity.getBody() == null) {
                return res;
            } else {
                return (List<RankVO>) responseEntity.getBody();
            }
        } catch (Exception e) {
            return res;
        }
    }
}
