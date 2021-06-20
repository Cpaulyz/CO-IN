package com.cosin.model.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@ApiModel("用户VO")
@NoArgsConstructor
@Accessors( chain = true)
public class UserVO {
    private String email;
    private String username;
    private String password;
}
