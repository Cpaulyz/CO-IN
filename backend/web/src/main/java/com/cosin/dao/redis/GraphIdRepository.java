package com.cosin.dao.redis;

import com.cosin.dao.neo4j.NodeRepository;
import com.cosin.dao.neo4j.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author chenyanze
 * @description 用于获取自增id，通过redis
 */
@Repository
public class GraphIdRepository {
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    NodeRepository nodeRepository;
    @Autowired
    RelationRepository relationRepository;

    /**
     * 根据projectId，生成project中唯一的自增nodeId
     * @param projectId
     * @return 自增id
     */
    public Long generateNodeIdByProjectId(int projectId){
        long tmp = redisTemplate.opsForHash().increment("nodeId",""+projectId,1);
        if(tmp==1){
            int maxNodeId = nodeRepository.getMaxNodeId(projectId);
            System.out.println("now nodeId max is "+maxNodeId);
            if (maxNodeId!=0){
                setNodeIdByProjectId(projectId, (long) maxNodeId);
                tmp = redisTemplate.opsForHash().increment("nodeId",""+projectId,1);
            }
        }
        return tmp;
    }

    /**
     * 根据projectId，生成project中唯一的自增relationId
     * @param projectId
     * @return 自增id
     */
    public Long generateRelationIdByProjectId(int projectId){
        long tmp = redisTemplate.opsForHash().increment("relationId",""+projectId,1);
        if(tmp==1){
            int maxRelId = relationRepository.getMaxRelationId(projectId);
            System.out.println("now relId max is "+maxRelId);
            if (maxRelId!=0){
                setRelationIdByProjectId(projectId, (long) maxRelId);
                tmp = redisTemplate.opsForHash().increment("relationId",""+projectId,1);
            }
        }
        return tmp;
    }

    /**
     * 设置nodeId
     * @param projectId
     * @param nowMaxNodeId 现在最大的nodeId
     */
    public void setNodeIdByProjectId(int projectId,Long nowMaxNodeId){
        redisTemplate.opsForHash().put("nodeId",""+projectId,""+nowMaxNodeId);
    }

    /**
     * 设置relationId
     * @param projectId
     * @param nowMaxRelationId 现在最大的relationId
     */
    public void setRelationIdByProjectId(int projectId,Long nowMaxRelationId){
        redisTemplate.opsForHash().put("relationId",""+projectId,""+nowMaxRelationId);
    }
}
