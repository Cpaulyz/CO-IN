package com.cosin.serviceImpl;

import com.cosin.dao.mysql.GraphLayoutMapper;
import com.cosin.dao.mysql.LayoutMapper;
import com.cosin.model.dto.LayoutDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.po.GraphLayoutPO;
import com.cosin.model.po.NodeLayoutPO;
import com.cosin.model.vo.ResponseVO;
import com.cosin.service.LayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LayoutServiceImpl implements LayoutService {
    @Autowired
    LayoutMapper nodeLayoutMapper;
    @Autowired
    GraphLayoutMapper graphLayoutMapper;

    @Override
    @Cacheable(key = "#projectId",value = "layoutOfProject")
    public List<LayoutDTO> getLayoutByProjectId(int projectId) {
        List<LayoutDTO> res = new ArrayList<>();
        for (LayoutTypeEnum type : LayoutTypeEnum.values()) {
            List<NodeLayoutPO> nodeLayoutPOS = nodeLayoutMapper.listLayoutByProjectIdAndType(projectId, type);
            LayoutDTO layoutDTO = new LayoutDTO().setType(type).setProjectId(projectId);
            for (NodeLayoutPO nodeLayoutPO : nodeLayoutPOS) {
                layoutDTO.addNode(nodeLayoutPO);
            }
            GraphLayoutPO graphLayoutPO = graphLayoutMapper.getGraphLayoutByProjectIdAndType(projectId,type);
            if(graphLayoutPO!=null){
                // graphLayoutPO可能是null，也就是刚初始化的时候，这时候不操作，直接用默认值
                layoutDTO.setWidth(graphLayoutPO.getWidth());
                layoutDTO.setHeight(graphLayoutPO.getHeight());
            }
            res.add(layoutDTO);
        }
        return res;
    }

    @Override
    @CacheEvict(key = "#layoutDTO.projectId",value = "layoutOfProject")
    public ResponseVO saveLayout(LayoutDTO layoutDTO) {
        // TODO:这里操作有点多，事务性待保证
        int res = 0;
        GraphLayoutPO graphLayoutPO = new GraphLayoutPO(layoutDTO);
        graphLayoutMapper.insertOrUpdateGraphLayout(graphLayoutPO);
        List<NodeLayoutPO> nodeLayoutPOS = layoutDTO.generateNodeLayoutPOS();
        for(NodeLayoutPO nodeLayoutPO:nodeLayoutPOS){
            res += nodeLayoutMapper.insertOrUpdateLayout(nodeLayoutPO);
        }
        return ResponseVO.buildSuccess("更新成功");
    }

    @Override
    @CacheEvict(key = "#projectId",value = "layoutOfProject")
    public ResponseVO deleteLayoutByProjectId(int projectId) {
        int res = nodeLayoutMapper.deleteLayoutByProjectId(projectId);
        return ResponseVO.buildSuccess();
    }

    @Override
    @CacheEvict(key = "#projectId",value = "layoutOfProject")
    public ResponseVO deleteLayoutByNodeIdAndProjectId(long nodeId, int projectId) {
        int res = nodeLayoutMapper.deleteLayoutByNodeIdAndProjectId(nodeId, projectId);
        return ResponseVO.buildSuccess();
    }

    @Override
    public List<LayoutDTO> getLayoutByProjectIdAndNodeId(int projectId, List<Long> nodeIds) {
        System.out.println(nodeIds);
        List<LayoutDTO> res = new ArrayList<>();
        for (LayoutTypeEnum type : LayoutTypeEnum.values()) {
            LayoutDTO layoutDTO = new LayoutDTO().setType(type).setProjectId(projectId);
            if(nodeIds.size()!=0){
                List<NodeLayoutPO> nodeLayoutPOS = nodeLayoutMapper.listLayoutByProjectIdAndNodeIdsAndType(projectId, nodeIds,type);
                for (NodeLayoutPO nodeLayoutPO : nodeLayoutPOS) {
                    layoutDTO.addNode(nodeLayoutPO);
                }
            }

            GraphLayoutPO graphLayoutPO = graphLayoutMapper.getGraphLayoutByProjectIdAndType(projectId,type);
            if(graphLayoutPO!=null){
                // graphLayoutPO可能是null，也就是刚初始化的时候，这时候不操作，直接用默认值
                layoutDTO.setWidth(graphLayoutPO.getWidth());
                layoutDTO.setHeight(graphLayoutPO.getHeight());
            }
            res.add(layoutDTO);
        }
        return res;
    }
}
