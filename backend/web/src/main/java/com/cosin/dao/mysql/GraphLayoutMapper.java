package com.cosin.dao.mysql;


import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.po.GraphLayoutPO;
import com.cosin.model.po.NodeLayoutPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * graph_layout表根据project_id进行定位
 */
@Repository
@Mapper
public interface GraphLayoutMapper {

    @Select("select * from graph_layout where project_id=#{projectId} and layout_type=#{type}")
    @Results(id="layoutMap",value = {
            @Result(column = "project_id", property = "projectId"),
            @Result(column = "layout_type", property = "type")
    })
    GraphLayoutPO getGraphLayoutByProjectIdAndType(@Param("projectId") int projectId, @Param("type") LayoutTypeEnum type);


    @Insert("replace into graph_layout(project_id,layout_type,width,height)" +
            "values (#{projectId},#{type},#{width},#{height})")
    int insertOrUpdateGraphLayout(GraphLayoutPO graphLayoutPO);



}
