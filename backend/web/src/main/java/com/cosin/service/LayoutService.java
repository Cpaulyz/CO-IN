package com.cosin.service;

import com.cosin.model.dto.LayoutDTO;
import com.cosin.model.vo.ResponseVO;

import java.util.List;

public interface LayoutService {

    /**
     * 根据项目id获取项目布局信息
     * @param projectId 项目id
     * @return 布局信息列表，每个type一个，如果没有则不返回
     */
    List<LayoutDTO> getLayoutByProjectId(int projectId);

    /**
     * 保存或者更新项目布局
     * @param layoutDTO 项目布局
     * @return 是否成功·
     */
    ResponseVO saveLayout(LayoutDTO layoutDTO);

    /**
     * 删除项目布局
     * @param projectId 项目id
     * @return 是否成功
     */
    ResponseVO deleteLayoutByProjectId(int projectId);

    /**
     * 删除节点布局
     * @param nodeId nodeId
     * @param projectId projectId
     * @return 是否成功
     */
    ResponseVO deleteLayoutByNodeIdAndProjectId(long nodeId,  int projectId);

    /**
     * 根据项目id获取项目布局信息
     * @param projectId 项目id
     * @param nodeIds nodeId列表
     * @return 布局信息列表，每个type一个，如果没有则不返回
     */
    List<LayoutDTO> getLayoutByProjectIdAndNodeId(int projectId,List<Long> nodeIds);

}
