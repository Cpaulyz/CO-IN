package com.cosin.model.vo.qa;

import com.cosin.model.vo.GraphVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("回答VO")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class GraphAnswerVO {

    @ApiModelProperty(value = "回答")
    String text;

    @ApiModelProperty(value = "依据图谱")
    GraphVO graph;
}
