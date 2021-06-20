package com.cosin.model.vo.node;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("级联删除节点返回VO")
public class NodeRelVO {

    @ApiModelProperty(value = "NodeId，project唯一",example = "1")
    Long nodeId;

    @ApiModelProperty("所属项目id")
    int projectId;

    @ApiModelProperty("相关关系id")
    List<Long> relations;
}
