package com.cosin.dao.mysql;

import com.cosin.model.enums.LayoutTypeEnum;
import com.cosin.model.enums.ProjectStatusEnum;
import com.cosin.model.po.ProjectPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProjectMapper {
    @Select("select * from project where user_id=#{userId} limit #{currIndex},#{pageSize}")
    @Results(id = "projectMap", value = {
            @Result(column = "user_id", property = "userId"),
            @Result(column = "id", property = "projectId"),
            @Result(column = "layout_status", property = "layoutStatus"),
            @Result(column = "image_url", property = "image")
    })
    List<ProjectPO> listProjectsByUserId(@Param("userId")int userId,@Param("currIndex") int currIndex, @Param("pageSize") int pageSize);


    @Select("select count(1) from project where user_id=#{userId}")
    int countProjectsByUserId(int userId);


    @Insert("insert into project(name,description,user_id,status,image_url)" +
            "values (#{name},#{description},#{userId},#{status},#{image})")
    @Options(useGeneratedKeys = true, keyProperty = "projectId")
    int insertProject(ProjectPO projectPO);

    @Select("select * from project where id=#{projectId}")
    @ResultMap(value = "projectMap")
    ProjectPO getProjectByProjectId(int projectId);

    @Update("update project set name=#{name}" +
            " where id=#{projectId}")
    int updateProjectName(@Param("projectId") int projectId, @Param("name") String name);

    @Update("update project set description=#{description}" +
            " where id=#{projectId}")
    int updateProjectDescription(@Param("projectId") int projectId, @Param("description") String description);

    @Update("update project set image_url=#{image}" +
            " where id=#{projectId}")
    int updateProjectImage(@Param("projectId") int projectId, @Param("image") String image);

    @Update("update project set layout_status=#{layoutStatus}" +
            " where id=#{projectId}")
    int updateProjectLayout(@Param("projectId") int projectId, @Param("layoutStatus") LayoutTypeEnum layout);

    @Update("update project set status=#{status}" +
            " where id=#{projectId}")
    int updateProjectStatus(@Param("projectId") int projectId, @Param("status") ProjectStatusEnum status);



    @Select("select * from project where status='PUBLIC' limit #{currIndex},#{pageSize}")
    @ResultMap(value = "projectMap")
    List<ProjectPO> listPublicProjects(@Param("currIndex") int currIndex, @Param("pageSize") int pageSize);

    @Select("select count(1) from project where status='PUBLIC'")
    int countPublicProject();
}
