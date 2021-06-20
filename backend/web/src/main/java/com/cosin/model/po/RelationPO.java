package com.cosin.model.po;

import com.cosin.model.XMLObject.RelationXMLO;
import com.cosin.model.dto.RelationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.neo4j.ogm.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
@RelationshipEntity(type = "relation")
@NoArgsConstructor
@Accessors(chain = true)
public class RelationPO {

    @Id
    @GeneratedValue
    Long id;

    Long relationId;

    String name;

    @StartNode
    NodePO sourceNode;

    @EndNode
    NodePO targetNode;

    Long source;

    Long target;

    int width;

    int projectId;

    @Properties
    Map<String,String> properties;

    public RelationPO (RelationDTO relationDTO){
        BeanUtils.copyProperties(relationDTO,this);
    }

    public RelationPO (RelationXMLO relationXMLO){
        BeanUtils.copyProperties(relationXMLO,this);
    }
}
