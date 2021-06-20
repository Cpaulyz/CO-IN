package com.cosin.model.vo.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankItemVO {
    int rank;
    long nodeId;
    String name;
    double score;
}
