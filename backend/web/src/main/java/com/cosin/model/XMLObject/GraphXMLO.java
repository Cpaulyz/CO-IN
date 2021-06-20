package com.cosin.model.XMLObject;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "graph")
public class GraphXMLO {

    @ApiModelProperty("实体集合")
    @JacksonXmlElementWrapper(localName = "nodes")
    @JacksonXmlProperty(localName = "node")
    List<NodeXMLO> nodes;


    @ApiModelProperty("关系集合")
    @JacksonXmlElementWrapper(localName = "rels")
    @JacksonXmlProperty(localName = "rel")
    List<RelationXMLO> relations;
}
