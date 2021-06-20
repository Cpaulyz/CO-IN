package com.cosin.model.po;

import com.cosin.model.dto.ProjectDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectPO {
    private Integer projectId;
    private String name;
    private String description;
    private Integer userId;
    private LayoutTypeEnum layoutStatus;
    private ProjectStatusEnum status;

    public ProjectPO(ProjectDTO projectDTO){
        BeanUtils.copyProperties(projectDTO,this);
    }
}
