package com.cosin.model.vo.user;

import com.cosin.model.enums.UserIdentity;
import com.cosin.model.po.UserPO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@ApiModel("用户详情VO")
@NoArgsConstructor
public class UserInfoVO {
    private int id;
    private String email;
    private String username;

    public UserInfoVO(UserPO userPO){
        BeanUtils.copyProperties(userPO,this);
    }
}
