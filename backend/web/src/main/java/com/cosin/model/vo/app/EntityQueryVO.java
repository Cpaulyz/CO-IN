package com.cosin.model.vo.app;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel("实体查询VO")
@Data
@Accessors(chain = true)
public class EntityQueryVO {
    @ApiModelProperty("项目id")
    int projectId;
    @ApiModelProperty("实体名称")
    String entityName;
    @ApiModelProperty("是否要模糊匹配")
    boolean fuzzyQuery;
}
