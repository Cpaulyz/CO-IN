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

@ApiModel("项目描述VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectDescriptionVO {
    @ApiModelProperty("项目ID")
    private int projectId;
    @ApiModelProperty("项目描述")
    private String description;

    public ProjectDescriptionVO(ProjectDTO projectDTO){
        BeanUtils.copyProperties(projectDTO,this);
    }
}
