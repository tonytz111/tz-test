package com.tz.securitydemo.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类功能简述：用户实体类
 * 类功能详述：实现UserDetails接口
 *
 * @author tianzheng
 * @date 2020/2/20 12:12
 */
@Slf4j
public class Users implements UserDetails {
    private int id;
    private String userName;
    private String userPWD;

    private String userRole;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPWD() {
        return userPWD;
    }

    public void setUserPWD(String userPWD) {
        this.userPWD = userPWD;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        GrantedAuthority grantedAuthority = new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return getUserRole();
            }
        };

        log.debug("====="+grantedAuthority.getAuthority()+"=====");

        List<GrantedAuthority> rList = new ArrayList<GrantedAuthority>();
        rList.add(grantedAuthority);

        return rList;
    }

    @Override
    public String getPassword() {
        return this.userPWD;
    }

    @Override
    public String getUsername() {
        return this.userName;
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
