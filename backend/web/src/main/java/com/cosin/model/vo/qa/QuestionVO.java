package com.cosin.model.vo.qa;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@ApiModel("问题VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class QuestionVO {
    @ApiModelProperty(value = "所属项目id", example = "1")
    int projectId;

    @ApiModelProperty("问题")
    String text;
}
