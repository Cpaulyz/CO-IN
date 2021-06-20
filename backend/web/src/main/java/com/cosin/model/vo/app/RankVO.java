package com.cosin.model.vo.app;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("中心排名VO")
public class RankVO {
    String group;
    ArrayList<RankItemVO> items;
}
