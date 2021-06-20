package com.cosin.model.vo.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 照理来说，nodeId都是>0的，所以如果缺省值的话直接用默认的0即可
 */
public class NodePairVO {
    int src;
    int target;
}
