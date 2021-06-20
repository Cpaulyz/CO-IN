package com.cosin.dao.neo4j;

import com.cosin.model.po.NodePO;
import com.cosin.model.po.RelationPO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends Neo4jRepository<RelationPO, Long> {

    @CacheEvict(key = "#a0.projectId", value = "relsOfProjects")
    RelationPO save(RelationPO relationPO);

    @Cacheable(key = "#a0", value = "relsOfProjects")
    List<RelationPO> findByProjectId(int projectId);

    //    废弃，仅测试用
    @CacheEvict(key = "#a0.projectId", value = "relsOfProjects")
    @Query("match (n:node{nodeId::#{#rel.source},projectId::#{#rel.projectId}}) match (m:node{nodeId::#{#rel.target},projectId::#{#rel.projectId}})" +
            "create(n)-[r:relation{relationId::#{#rel.relationId},name::#{#rel.name},width::#{#rel.width},projectId::#{#rel.projectId}}]->(m) return n,r,m")
    RelationPO saveBySourceAndTarget(@Param("rel") RelationPO relationPO);

    //    废弃，仅测试用
    @CacheEvict(key = "#a0.projectId", value = "relsOfProjects")
    @Query("match (n)-[r:relation{relationId::#{#rel.relationId},projectId::#{#rel.projectId}}]->(m) " +
            "set r.name=:#{#rel.name}, r.width=:#{#rel.width}  return n,r,m")
    RelationPO updateByRel(@Param("rel") RelationPO relationPO);


    @CacheEvict(key = "#a0.projectId", value = "relsOfProjects")
    @Query("match (n)-[r:relation{relationId::#{#rel.relationId},projectId::#{#rel.projectId}}]->(m) " +
            "delete r")
    void deleteByRelation(@Param("rel") RelationPO relationPO);

    @CacheEvict(key = "#a0", value = "relsOfProjects")
    @Query("match (n)-[r:relation{projectId:{projectId}}]->(m) " +
            "delete r")
    void deleteByProjectId(@Param("projectId") int projectId);

    /**
     * 根据源节点或者目标节点，寻找与之相关的relation
     *
     * @param nodePO 源节点或者目标节点
     * @return 相关的relation列表
     */
    @Query("match (m)-[r]-(n:node{nodeId::#{#nodePO.nodeId},projectId::#{#nodePO.projectId}}) return m,r,n")
    List<RelationPO> listBySourceOrTarget(@Param("nodePO") NodePO nodePO);

    @Query("match (n)-[r:relation{relationId:{relationId},projectId:{projectId}}]->(m) " +
            "return id(r)")
    long findIdByRelationIdAndProjectId(@Param("relationId") long relationId, @Param("projectId") int projectId);

    /**
     * 根据名称找相关的实体和关系，用于实体查询
     */
    @Query("match (n:node{projectId:{projectId}})-[r]-(m) where n.name=~{sourceName} return n,r,m")
    List<RelationPO> findBySourceName(@Param("projectId") int projectId, @Param("sourceName") String name);

    /**
     * 用于关系查询
     *
     * @param projectId  项目约束
     * @param name       关系名称
     * @param sourceName 源节点名，若无为.*
     * @param targetName 目标节点名，若无为.*
     */
    @Query("MATCH (n:node)-[r{name:{relName},projectId:{projectId}}]->(m:node) " +
            "where n.name=~{sourceName} and m.name=~{targetName} RETURN n,r,m")
    List<RelationPO> findByProjectIdAndNameAndTargetAndSourceFuzzy(@Param("projectId") int projectId, @Param("relName") String name,
                                                                   @Param("sourceName") String sourceName, @Param("targetName") String targetName);

    @Query("MATCH (n:node{projectId:{projectId},nodeId:{sourceId}})-[r]-(m:node{nodeId:{targetId}}) " +
            "RETURN n,r,m")
    RelationPO findByProjectIdAndTargetIdAndSourceId(@Param("projectId") int projectId,
                                                     @Param("sourceId") int sourceId,
                                                     @Param("targetId") int targetId);

    @Query("match ()-[r{projectId:{projectId}}]->() return distinct(r.name)")
    List<String> findRelationNameByProjectId(@Param("projectId") int projectId);


    @Query("MATCH ()-[r:relation{projectId:{projectId}}]->() return max(r.relationId)")
    int getMaxRelationId(@Param("projectId") int projectId);
}
