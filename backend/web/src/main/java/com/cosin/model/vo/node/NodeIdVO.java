package com.cosin.model.vo.node;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 删除Node
 */
@ApiModel("删除node类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NodeIdVO {
    @ApiModelProperty(value = "NodeId，project唯一",example = "1")
    long nodeId;

    @ApiModelProperty("所属项目id")
    int projectId;
}
