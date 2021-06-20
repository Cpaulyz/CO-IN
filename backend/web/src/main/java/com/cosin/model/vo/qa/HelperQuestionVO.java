package com.cosin.model.vo.qa;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("纯问题VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class HelperQuestionVO {
    @ApiModelProperty("问题")
    String text;
}
