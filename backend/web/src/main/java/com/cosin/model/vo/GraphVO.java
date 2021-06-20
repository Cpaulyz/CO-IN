package com.cosin.model.vo;

import com.cosin.model.dto.LayoutDTO;
import com.cosin.model.dto.NodeDTO;
import com.cosin.model.dto.RelationDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("知识图谱VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class GraphVO {

    @ApiModelProperty(value = "所属项目id",example = "1")
    int projectId;

    @ApiModelProperty("布局信息")
    List<LayoutDTO> layout;

    @ApiModelProperty("实体集合")
    List<NodeDTO> nodes;

    @ApiModelProperty("关系集合")
    List<RelationDTO> relations;
}
