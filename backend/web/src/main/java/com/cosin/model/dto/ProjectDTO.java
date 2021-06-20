package com.cosin.model.dto;

import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import com.cosin.model.po.ProjectPO;
import com.cosin.model.vo.project.ProjectCreateVO;
import com.cosin.model.vo.project.ProjectDescriptionVO;
import com.cosin.model.vo.project.ProjectDetailVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

@ApiModel("项目VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectDTO {
    private Integer projectId;
    private String name;
    private String description;
    private Integer userId;
    private String xml;
    private LayoutTypeEnum layoutStatus;
    private ProjectStatusEnum status;

    public ProjectDTO(ProjectPO projectPO){
        BeanUtils.copyProperties(projectPO,this);
    }

    public ProjectDTO(ProjectDetailVO projectDetailVO){BeanUtils.copyProperties(projectDetailVO,this);}

    public ProjectDTO(ProjectDescriptionVO projectDescriptionVO){BeanUtils.copyProperties(projectDescriptionVO,this);}

    public ProjectDTO(ProjectCreateVO projectCreateVO){BeanUtils.copyProperties(projectCreateVO,this);}

}
