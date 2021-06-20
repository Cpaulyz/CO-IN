package com.cosin.model.vo.relation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.Properties;

import java.util.Map;

/**
 * 关系
 */
@ApiModel("关系VO")
@Data
@NoArgsConstructor
public class RelationVO {

    @ApiModelProperty(value = "relationId，添加时不传",example = "1")
    Long relationId;

    @ApiModelProperty("关系名称")
    String name;

    @ApiModelProperty(value = "实体1",example = "1")
    Long source;

    @ApiModelProperty(value = "实体2",example = "1")
    Long target;

    @ApiModelProperty(value = "宽度",example = "1")
    int width;

    @ApiModelProperty("所属项目id")
    int projectId;

    @ApiModelProperty("可变属性")
    Map<String,String> properties;
}
