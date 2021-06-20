package com.cosin.model.vo.project;

import com.cosin.model.enums.LayoutTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("项目布局VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectLayoutVO {

    @ApiModelProperty("项目ID")
    private Integer projectId;
    @ApiModelProperty("所属用户")
    private Integer userId;
    @ApiModelProperty("当前布局")
    private LayoutTypeEnum layoutStatus;
}
