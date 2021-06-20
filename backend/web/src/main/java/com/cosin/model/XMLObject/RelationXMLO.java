package com.cosin.model.XMLObject;

import com.cosin.model.po.RelationPO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RelationXMLO {

    @ApiModelProperty(value = "relationId，project唯一",example = "1")
    Long relationId;

    @ApiModelProperty("关系名称")
    String name;

    @ApiModelProperty(value = "实体1",example = "1")
    Long source;

    @ApiModelProperty(value = "实体2",example = "1")
    Long target;

    @ApiModelProperty(value = "宽度",example = "1")
    int width;

    @ApiModelProperty("可变属性")
    Map<String,String> properties;

    public RelationXMLO (RelationPO relationPO){
        BeanUtils.copyProperties(relationPO,this);
        this.source = relationPO.getSourceNode()==null?0:relationPO.getSourceNode().getNodeId();
        this.target = relationPO.getTargetNode()==null?0:relationPO.getTargetNode().getNodeId();
    }
}
