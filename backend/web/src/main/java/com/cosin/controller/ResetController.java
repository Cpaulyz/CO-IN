package com.cosin.controller;

import com.cosin.dao.redis.GraphIdRepository;
import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.neo4j.RelationRepository;
import com.cosin.model.dto.NodeDTO;
import com.cosin.model.dto.RelationDTO;
import com.cosin.service.GraphService;
import com.cosin.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 仅用于开发时重置neo4j用
 */
@RestController
@RequestMapping("/admin")
public class ResetController {

    @Autowired
    RelationRepository relationRepository;
    @Autowired
    NodeRepository nodeRepository;
    @Autowired
    GraphService graphService;
    @Autowired
    GraphIdRepository graphIdRepository;

    @GetMapping("/reset/{projectId}")
    public ResponseEntity reset(@PathVariable("projectId") int projectId){
        relationRepository.deleteByProjectId(projectId);
        nodeRepository.deleteByProjectId(projectId);
        graphIdRepository.setRelationIdByProjectId(projectId,0L);
        graphIdRepository.setNodeIdByProjectId(projectId,0L);
        NodeDTO nodeDTO1 = new NodeDTO().setName("皮卡丘").setGroup("宠物").setRadius(1).setFigure("circle").setTextSize("15").setColor("").setProjectId(projectId);
        NodeDTO nodeDTO2 = new NodeDTO().setName("杰尼龟").setGroup("宠物").setRadius(2).setFigure("circle").setTextSize("15").setColor("").setProjectId(projectId);
        NodeDTO nodeDTO3 = new NodeDTO().setName("小智").setGroup("人物").setRadius(1).setFigure("circle").setTextSize("15").setColor("").setProjectId(projectId);
        NodeDTO nodeDTO4 = new NodeDTO().setName("cyz").setGroup("人物").setRadius(3).setFigure("circle").setTextSize("15").setColor("").setProjectId(projectId);
        Map<String,String> properties = new HashMap<>();
        properties.put("生日","12-20");
        properties.put("身高","120");
        nodeDTO1.setProperties(properties);
        NodeDTO node1 = graphService.insertNode(nodeDTO1);
        NodeDTO node2 =graphService.insertNode(nodeDTO2);
        NodeDTO node3 =graphService.insertNode(nodeDTO3);
        NodeDTO node4 = graphService.insertNode(nodeDTO4);
        System.out.println(node1);
        System.out.println(node2);
        System.out.println(node3);
        System.out.println(node4);
        RelationDTO relationDTO = new RelationDTO();
        relationDTO.setName("主人");
        relationDTO.setProjectId(projectId);
        relationDTO.setSource(node4.getNodeId());
        relationDTO.setTarget(node1.getNodeId());
        relationDTO.setWidth(2);
        RelationDTO relationDTO1 =graphService.insertRelation(relationDTO);
        System.out.println(relationDTO1);
        relationDTO.setSource(node3.getNodeId());
        relationDTO.setWidth(1);
        relationDTO1 =graphService.insertRelation(relationDTO);
        System.out.println(relationDTO1);
        relationDTO.setTarget(node2.getNodeId());
        relationDTO1 =graphService.insertRelation(relationDTO);
        System.out.println(relationDTO1);
        relationDTO.setSource(node1.getNodeId());
        relationDTO.setName("朋友");
        relationDTO1 =graphService.insertRelation(relationDTO);
        System.out.println(relationDTO1);
        return ResponseUtils.success(true);
    }
}
