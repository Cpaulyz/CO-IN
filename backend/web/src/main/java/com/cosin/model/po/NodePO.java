package com.cosin.model.po;

import com.cosin.model.XMLObject.NodeXMLO;
import com.cosin.model.dto.NodeDTO;
import lombok.*;
import lombok.experimental.Accessors;
import org.neo4j.ogm.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

@NodeEntity(label = "node")
@Data
@ToString
@Accessors(chain = true)
public class NodePO {

    @Id
    @GeneratedValue
    Long id;

    @Property("nodeId")
    Long nodeId;

    @Property("name")
    String name;

    @Property("group")
    String group;

    @Property("radius")
    int radius;

    @Property("projectId")
    int projectId;

    @Property("color")
    String color;

    @Property("textSize")
    String textSize;

    @Property("figure")
    String figure;

    @Properties
    Map<String,String> properties;

    public NodePO(){
        properties = new HashMap<>();
    }

    public NodePO(NodeDTO nodeDTO){
        BeanUtils.copyProperties(nodeDTO,this);
    }

    public NodePO(NodeXMLO nodeXMLO){
        BeanUtils.copyProperties(nodeXMLO,this);
    }
}
