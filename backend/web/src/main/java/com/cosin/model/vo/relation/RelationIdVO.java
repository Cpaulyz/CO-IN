package com.cosin.model.vo.relation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系ID
 */
@ApiModel("关系ID VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationIdVO {
    @ApiModelProperty(value = "relationId，project唯一",example = "1")
    Long relationId;

    @ApiModelProperty("所属项目id")
    int projectId;
}
