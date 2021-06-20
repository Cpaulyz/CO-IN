package com.cosin.service;

import com.cosin.COINWebApplication;
import com.cosin.model.vo.GraphVO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.FlaskAnswerVO;
import com.cosin.model.vo.app.RankVO;
import com.cosin.model.vo.qa.GraphAnswerVO;
import com.cosin.model.vo.qa.QuestionVO;
import com.cosin.serviceImpl.QAServiceImpl;
import com.cosin.util.ResponseUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = COINWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(value = "transactionManager")
public class QAServiceTest {
    @InjectMocks
    QAServiceImpl qaService;
    @Mock
    RestTemplate restTemplate;
    @Mock
    GraphService graphService;

    private static final int TEST_PROJECT_ID = 0;

    @Test
    public void testQaInitial(){
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                new FlaskAnswerVO("text",new ArrayList<>(),true)));
        ResponseVO responseVO = qaService.qaInitial(TEST_PROJECT_ID);
        assert responseVO.isSuccess();
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                (FlaskAnswerVO)null));
        responseVO = qaService.qaInitial(TEST_PROJECT_ID);
        assert !responseVO.isSuccess();
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                new FlaskAnswerVO("text",new ArrayList<>(),false)));
        responseVO = qaService.qaInitial(TEST_PROJECT_ID);
        assert !responseVO.isSuccess();
    }

    @Test
    public void testQuestion(){
        QuestionVO questionVO = new QuestionVO().setProjectId(TEST_PROJECT_ID).setText("question");
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                new FlaskAnswerVO("text",new ArrayList<>(),true)));
        Mockito.when(graphService.getGraphFromNodePair(Mockito.eq(TEST_PROJECT_ID),Mockito.any())).thenReturn(new GraphVO());
        GraphAnswerVO graphAnswerVO = qaService.question(questionVO);
        assert graphAnswerVO!=null;
        assert graphAnswerVO.getText().equals("text");
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                (FlaskAnswerVO)null));
        graphAnswerVO = qaService.question(questionVO);
        assert graphAnswerVO==null;
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                new FlaskAnswerVO("text",new ArrayList<>(),false)));
        graphAnswerVO = qaService.question(questionVO);
        assert graphAnswerVO==null;
    }

    @Test
    public void testVerifyInitial(){
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                new FlaskAnswerVO("text",new ArrayList<>(),true)));
        boolean res  = qaService.verifyInitial(TEST_PROJECT_ID);
        assert res;
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                (FlaskAnswerVO)null));
        res = qaService.verifyInitial(TEST_PROJECT_ID);
        assert !res;
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(FlaskAnswerVO.class))).thenReturn(ResponseUtils.success(
                new FlaskAnswerVO("text",new ArrayList<>(),false)));
        res  = qaService.verifyInitial(TEST_PROJECT_ID);
        assert !res;
    }

    @Test
    public void testHotQuestion(){
        List<String> qs = new ArrayList<>();
        qs.add("1");
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(List.class))).thenReturn(ResponseUtils.success(
                qs));
        List<String> res  = qaService.hotQuestion(TEST_PROJECT_ID);
        assert res.size()==1;
        assert "1".equals(res.get(0));
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(List.class))).thenReturn(ResponseUtils.success(
                (List)null));
        res  = qaService.hotQuestion(TEST_PROJECT_ID);
        assert res.size()==0;
    }

    @Test
    public void testCentrality(){
        List<RankVO> rankVOS = new ArrayList<>();
        rankVOS.add(new RankVO());
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.eq(List.class))).thenReturn(ResponseUtils.success(
                rankVOS));
        List<RankVO> res  = qaService.centrality(TEST_PROJECT_ID);
        assert res.size()==1;
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(List.class))).thenReturn(ResponseUtils.success(
                (List)null));
        res  = qaService.centrality(TEST_PROJECT_ID);
        assert res.size()==0;
    }
}
