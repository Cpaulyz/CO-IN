package com.cosin.model.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel("token")
@Data
@Accessors(chain = true)
public class UserTokenVO {
    String token;
}
