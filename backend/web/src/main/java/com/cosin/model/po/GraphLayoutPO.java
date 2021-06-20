package com.cosin.model.po;

import com.cosin.model.dto.LayoutDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GraphLayoutPO {
    int id;
    int projectId;
    LayoutTypeEnum type;
    double width;
    double height;

    public GraphLayoutPO(LayoutDTO layoutDTO){
        this.projectId=layoutDTO.getProjectId();
        this.type = layoutDTO.getType();
        this.width=layoutDTO.getWidth();
        this.height=layoutDTO.getHeight();
    }
}
