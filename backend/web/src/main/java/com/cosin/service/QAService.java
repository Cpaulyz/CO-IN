package com.cosin.service;

import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.RankVO;
import com.cosin.model.vo.qa.GraphAnswerVO;
import com.cosin.model.vo.qa.QuestionVO;

import java.util.List;

public interface QAService {
    /**
     * 初始化问答图谱
     * @param projectId
     * @return success & message
     */
    ResponseVO qaInitial(int projectId);

    /**
     * 知识图谱问答
     * @param questionVO
     * @return 答案和子图 null的话为未知错误
     */
    GraphAnswerVO question(QuestionVO questionVO);

    /**
     * 验证知识图谱是否已初始化
     * @param projectId
     * @return true or false
     */
    boolean verifyInitial(int projectId);

    /**
     * 获取某项目的热门查询
     * @param projectId
     * @return
     */
    List hotQuestion(int projectId);


    /**
     * 中心度计算 PageRank
     * @param projectId
     * @return
     */
    List<RankVO> centrality(int projectId);
}
