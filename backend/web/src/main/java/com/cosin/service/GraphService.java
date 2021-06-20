package com.cosin.service;

import com.cosin.model.dto.*;
import com.cosin.model.vo.GraphVO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.model.vo.app.EntityQueryVO;
import com.cosin.model.vo.node.NodePairVO;
import com.cosin.model.vo.node.NodeRelNodeVO;
import com.cosin.model.vo.node.NodeRelVO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.NoSuchElementException;

public interface GraphService {
    /**
     * 根据id查找知识图谱
     */
    GraphVO getGraphByProjectId(int projectId);

    /**
     * 插入实体
     */
    NodeDTO insertNode(NodeDTO nodeDTO);

    /**
     * 插入关系
     * 检查source和target是否存在，不存在的话抛出异常
     */
    RelationDTO insertRelation(RelationDTO relationDTO) throws NoSuchElementException;

    /**
     * 更新实体
     */
    NodeDTO updateNode(NodeDTO nodeDTO);

    /**
     * 更新关系
     */
    RelationDTO updateRelation(RelationDTO relationDTO);

    /**
     * 非级联删除实体
     */
    ResponseVO deleteNode(NodeDTO nodeDTO);

    /**
     * 级联删除实体，会删除携带关系
     * @return 返回删除节点id以及级联删除相关关系id
     */
    NodeRelVO cascadeDeleteNode(NodeDTO nodeDTO);

    /**
     * 删除关系
     */
    ResponseVO deleteRelation(RelationDTO relationDTO);

    /**
     * 实体查询，允许模糊匹配
     * @param entityQueryVO
     * @return
     */
    GraphVO entityQuery(EntityQueryVO entityQueryVO);

    /**
     * 关系查询
     * @param nodeRelNodeVO
     * @return
     */
    GraphVO relQuery(NodeRelNodeVO nodeRelNodeVO);


    /**
     * 获取某项目下可能的所有关系名称
     * @param projectId
     * @return
     */
    List<String> findRelationNameByProjectId(int projectId);

    /**
     * 根据nodePair找子图
     * @param projectId 项目id
     * @param nodePairs src,tar对
     * @return
     */
    GraphVO getGraphFromNodePair(int projectId,List<NodePairVO> nodePairs);


    /**
     * 查询项目
     * @param sentence
     * @return
     */
    String projectQuery(String sentence);

}
