package com.cosin.model.vo.node;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("关系查询VO")
public class NodeRelNodeVO {
    @ApiModelProperty("项目id")
    int projectId;

    @ApiModelProperty("源名称")
    String sourceName;
    @ApiModelProperty("关系名称")
    String relName;
    @ApiModelProperty("目标名称")
    String targetName;
}
