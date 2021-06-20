package com.cosin.model.vo.app;

import com.cosin.model.vo.node.NodePairVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 用于与Python微服务通信
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FlaskAnswerVO {
    String text;
    List<NodePairVO> nodes;
    boolean success;
}
