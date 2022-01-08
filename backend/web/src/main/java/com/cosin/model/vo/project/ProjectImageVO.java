package com.cosin.model.vo.project;

import com.cosin.model.dto.ProjectDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

@ApiModel("项目主图VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectImageVO {
    @ApiModelProperty("项目ID")
    private int projectId;
    @ApiModelProperty("项目主图")
    private String image;
}
