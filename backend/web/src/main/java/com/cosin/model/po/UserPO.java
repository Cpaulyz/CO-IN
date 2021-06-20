package com.cosin.model.po;

import com.cosin.model.enums.UserIdentity;
import com.cosin.model.vo.user.UserInfoVO;
import com.cosin.model.vo.user.UserLoginVO;
import com.cosin.model.vo.user.UserVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserPO implements UserDetails {
    private int id;
    private String email;
    private String username;
    private String password;
    private UserIdentity role;

    public UserPO(UserVO userVO){
        BeanUtils.copyProperties(userVO,this);
    }

    public UserPO(UserInfoVO userInfoVO){
        BeanUtils.copyProperties(userInfoVO,this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
