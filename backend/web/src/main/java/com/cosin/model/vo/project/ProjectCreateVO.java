package com.cosin.model.vo.project;

import com.cosin.model.dto.ProjectDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

@ApiModel("项目创建VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectCreateVO {

    @ApiModelProperty("项目名称")
    private String name;
    @ApiModelProperty("项目描述")
    private String description;
    @ApiModelProperty("所属用户")
    private Integer userId;
    @ApiModelProperty("导入XML")
    private String xml;
    @ApiModelProperty("主图url")
    private String image;
    @ApiModelProperty("公私有")
    private ProjectStatusEnum status;
}
