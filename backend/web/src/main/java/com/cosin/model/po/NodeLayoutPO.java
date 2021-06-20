package com.cosin.model.po;

import com.cosin.model.enums.LayoutTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NodeLayoutPO {
    int id;

    long nodeId;

    int projectId;

    double xAxis;

    double yAxis;

    LayoutTypeEnum type;
}
