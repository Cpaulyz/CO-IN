package com.cosin.model.dto;

import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.po.NodeLayoutPO;
import com.cosin.model.vo.LayoutVO;
import com.cosin.model.vo.node.NodeLayoutVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 布局DTO
 */
@Data
@Accessors(chain = true)
public class LayoutDTO {
    int projectId;

    LayoutTypeEnum type;

    double width;

    double height;

    List<NodeLayoutVO> nodes;


    /**
     * 插入node，需要保证projectId一致，这里不做检查了
     * @param layoutPO
     */
    public void addNode(NodeLayoutPO layoutPO){
        nodes.add(new NodeLayoutVO(layoutPO.getNodeId(),layoutPO.getXAxis(),layoutPO.getYAxis()));
    }

    /**
     * 生成NodeLayoutPO列表
     * @return NodeLayoutPO列表
     */
    public List<NodeLayoutPO> generateNodeLayoutPOS(){
        List<NodeLayoutPO> res = new ArrayList<>();
        for(NodeLayoutVO nodeLayout :nodes){
            NodeLayoutPO nodeLayoutPO = new NodeLayoutPO()
                    .setProjectId(projectId)
                    .setType(type)
                    .setXAxis(nodeLayout.getXAxis())
                    .setYAxis(nodeLayout.getYAxis())
                    .setNodeId(nodeLayout.getNodeId());
            res.add(nodeLayoutPO);
        }
        return res;
    }

    public LayoutDTO(){
        nodes = new ArrayList<>();
    }

    public LayoutDTO(LayoutVO layoutVO){
        nodes = new ArrayList<>();
        BeanUtils.copyProperties(layoutVO,this);
    }
}
