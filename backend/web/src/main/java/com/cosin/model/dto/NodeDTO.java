package com.cosin.model.dto;

import com.cosin.model.po.NodePO;
import com.cosin.model.vo.node.NodeIdVO;
import com.cosin.model.vo.node.NodeVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
//import org.neo4j.ogm.annotation.Property;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class NodeDTO {

//    @ApiModelProperty("id，全局唯一")
//    Long id;

    Long nodeId;

    String name;

    String group;

    int radius;

    int projectId;

    String color;

    String textSize;

    String figure;

    Map<String,String> properties;

    public NodeDTO(){
        properties = new HashMap<>();
    }

    public NodeDTO(NodePO nodePO){
        BeanUtils.copyProperties(nodePO,this);
    }

    public NodeDTO(NodeIdVO nodeIdVO){
        BeanUtils.copyProperties(nodeIdVO,this);
    }

    public NodeDTO(NodeVO nodeVO){BeanUtils.copyProperties(nodeVO,this);}

}
