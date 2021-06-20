package com.cosin.model.vo.node;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
/**
 * 实体
 */
@ApiModel("实体VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class NodeVO {
    @ApiModelProperty(value = "NodeId，添加时不传",example = "1")
    Long nodeId;

    @ApiModelProperty("所属项目id")
    int projectId;

    @ApiModelProperty("实体名称")
    String name;

    @ApiModelProperty("实体类型")
    String group;

    @ApiModelProperty("半径")
    int radius;

    @ApiModelProperty("颜色")
    String color;

    @ApiModelProperty("字体大小")
    String textSize;

    @ApiModelProperty("形状")
    String figure;

    @ApiModelProperty("动态属性")
    Map<String,String> properties;

}
