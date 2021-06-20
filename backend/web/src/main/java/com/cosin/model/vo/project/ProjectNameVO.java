package com.cosin.model.vo.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("项目名称VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectNameVO {
    @ApiModelProperty("项目ID")
    private int projectId;

    @ApiModelProperty("项目名称")
    private String name;
}
