package com.cosin.dao.neo4j;

import com.cosin.model.po.NodePO;
import com.cosin.model.po.NodeWithRelationPO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends Neo4jRepository<NodePO,Long> {

    @CacheEvict(key = "#a0.projectId", value = "nodesOfProjects")
    NodePO save(NodePO nodePO);

    @Cacheable(key = "#a0",value = "nodesOfProjects")
    List<NodePO> findByProjectId(int projectId);

    NodePO findByNodeIdAndProjectId(Long nodeId, int projectId);

//    /**
//     * 自己实现的update方法，该方法不会改变用户不可见字段，比使用save来update更加安全
//     */
//    @Query("MATCH (n:node{nodeId::#{#nodePO.nodeId},projectId::#{#nodePO.projectId}}) " +
//            "SET n.name = :#{#nodePO.name},n.group = :#{#nodePO.group}, n.val = :#{#nodePO.val} RETURN n")
//    NodePO updateByNode(@Param("nodePO") NodePO nodePO);


    @CacheEvict(key = "#a0.projectId", value = "nodesOfProjects")
    @Query("MATCH (n:node{nodeId::#{#nodePO.nodeId},projectId::#{#nodePO.projectId}}) delete n")
    void deleteByNode(@Param("nodePO") NodePO nodePO);

//    @Query("MATCH (n:node{nodeId::#{#nodeGraphic.nodeId},projectId::#{#nodeGraphic.projectId}}) " +
//            "SET n.xAxis = :#{#nodeGraphic.xAxis},n.yAxis = :#{#nodeGraphic.yAxis} RETURN n")
//    void updateNodeGraphic(@Param("nodeGraphic")NodeGraphicPO nodeGraphic);

    @CacheEvict(key = "#a0.projectId", value = "nodesOfProjects")
    @Query("MATCH (n:node{nodeId::#{#nodePO.nodeId},projectId::#{#nodePO.projectId}}) detach delete n")
//    @Query("MATCH (n:node{nodeId::#{#nodePO.nodeId},projectId::#{#nodePO.projectId}})-[r]-() delete r,n")
    void cascadeDeleteNode(@Param("nodePO") NodePO nodePO);

    @CacheEvict(key = "#a0", value = "nodesOfProjects")
    @Query("MATCH (n:node{projectId:{projectId}}) delete n")
    void deleteByProjectId(@Param("projectId") int projectId);

    @Query("match (n:node{projectId:{projectId}}) where n.name=~('.*'+$name+'.*') return n")
    List<NodePO> searchNodeByNameAndProjectId(@Param("name") String name,@Param("projectId")int projectId);

    @Query("match (n:node) where n.name=~$name return n")
    List<NodePO> findByNameFuzzy(@Param("name") String name);

    @Query("MATCH (n:node{projectId:{projectId}}) return max(n.nodeId)")
    int getMaxNodeId(@Param("projectId") int projectId);
}

