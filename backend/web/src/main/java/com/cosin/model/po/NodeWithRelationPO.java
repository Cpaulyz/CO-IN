package com.cosin.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ToString
@Accessors(chain = true)
public class NodeWithRelationPO {
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

    @Relationship(type = "relation",direction = Relationship.UNDIRECTED)
    Set<NodeWithRelationPO> nodes = new HashSet<>();
}
