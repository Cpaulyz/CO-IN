package com.cosin.dao.mysql;

import com.cosin.model.po.UserPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {

    @Insert("insert into t_user(name,password,email)" +
            "values (#{username},#{password},#{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(UserPO userPO);

    @Select("select * from t_user where name=#{username}")
    @Results(id="userMap",value = {
            @Result(column = "name", property = "username"),
    })
    UserPO getUserByUsername(String username);
}
