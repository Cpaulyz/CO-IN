package com.cosin.model.dto;

import com.cosin.model.po.RelationPO;
import com.cosin.model.vo.relation.RelationIdVO;
import com.cosin.model.vo.relation.RelationVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.neo4j.ogm.annotation.Properties;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * 关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RelationDTO {

//    @ApiModelProperty("id，全局唯一")
//    Long id;
    Long relationId;

    String name;

    Long source;

    Long target;

    int width;

    int projectId;

    Map<String,String> properties;

    public RelationDTO(RelationPO relationPO){
        BeanUtils.copyProperties(relationPO,this);
        this.source = relationPO.getSourceNode()==null?0:relationPO.getSourceNode().getNodeId();
        this.target = relationPO.getTargetNode()==null?0:relationPO.getTargetNode().getNodeId();
    }

    public RelationDTO(RelationIdVO relationIdVO){
        BeanUtils.copyProperties(relationIdVO,this);
    }

    public RelationDTO(RelationVO relationVO){
        BeanUtils.copyProperties(relationVO,this);
    }
}
