package com.cosin.model.vo;

import com.cosin.model.dto.LayoutDTO;
import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.vo.node.NodeLayoutVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("布局")
@Data
@NoArgsConstructor
public class LayoutVO {

    @ApiModelProperty("项目id")
    int projectId;

    @ApiModelProperty("布局占用宽")
    double width;

    @ApiModelProperty("布局占用高")
    double height;

    @ApiModelProperty("布局类型")
    LayoutTypeEnum type;

    @ApiModelProperty("节点布局信息")
    List<NodeLayoutVO> nodes;
}
