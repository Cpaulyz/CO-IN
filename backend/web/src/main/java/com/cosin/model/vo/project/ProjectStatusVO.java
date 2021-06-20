package com.cosin.model.vo.project;

import com.cosin.model.enums.ProjectStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjectStatusVO {
    @ApiModelProperty("项目id")
    private int projectId;
    @ApiModelProperty("公私有")
    private ProjectStatusEnum status;
}
