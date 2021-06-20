package com.cosin.dao.mysql;

import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.po.NodeLayoutPO;
import lombok.Data;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * layout表根据project_id和node_id进行定位
 */
@Repository
@Mapper
public interface LayoutMapper {

    @Select("select * from layout where project_id=#{projectId}")
    @Results(id = "layoutMap", value = {
            @Result(column = "project_id", property = "projectId"),
            @Result(column = "node_id", property = "nodeId"),
            @Result(column = "x_axis", property = "xAxis"),
            @Result(column = "y_axis", property = "yAxis")
    })
    List<NodeLayoutPO> listLayoutByProjectId(int projectId);

    @Select("select * from layout where project_id=#{projectId} and type=#{type}")
    @ResultMap("layoutMap")
    List<NodeLayoutPO> listLayoutByProjectIdAndType(@Param("projectId") int projectId, @Param("type") LayoutTypeEnum type);

    @Insert("replace into layout(project_id,node_id,x_axis,y_axis,type)" +
            "values (#{projectId},#{nodeId},#{xAxis},#{yAxis},#{type})")
    int insertOrUpdateLayout(NodeLayoutPO nodeLayoutPO);

    @Delete("delete from layout where project_id=#{projectId} and node_id=#{nodeId}")
    int deleteLayoutByNodeIdAndProjectId(@Param("nodeId") long nodeId, @Param("projectId") int projectId);

    @Delete("delete from layout where project_id=#{projectId}")
    int deleteLayoutByProjectId(int projectId);

    @Select({"<script>" +
             "select * from layout where project_id=#{projectId} and type=#{type} and node_id in " +
                "<foreach item = 'item' index = 'index' collection = 'nodes' open='(' separator=',' close=')'>" +
                "#{item}" +
                "</foreach>"+
             "</script>"})
    @ResultMap("layoutMap")
    List<NodeLayoutPO> listLayoutByProjectIdAndNodeIdsAndType(@Param("projectId") int projectId, @Param("nodes") List<Long> nodes,
                                                              @Param("type") LayoutTypeEnum type);
}
