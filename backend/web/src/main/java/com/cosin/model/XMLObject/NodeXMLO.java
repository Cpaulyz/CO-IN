package com.cosin.model.XMLObject;

import com.cosin.model.po.NodePO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.neo4j.ogm.annotation.Properties;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NodeXMLO {

    Long nodeId;

    String name;

    String group;

    int radius;

    int projectId;

    String color;

    String textSize;

    String figure;

    Map<String,String> properties;

    public NodeXMLO(NodePO nodePO){
        BeanUtils.copyProperties(nodePO,this);
    }
}
