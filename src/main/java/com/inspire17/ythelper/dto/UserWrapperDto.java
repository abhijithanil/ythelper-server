package com.inspire17.ythelper.dto;

import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.sec.YTGrantedAuthority;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserWrapperDto implements UserDetails {

    private UserEntity user;
    private Collection<? extends GrantedAuthority> authorities;

    public UserWrapperDto(UserEntity user) {
        this.user = user;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new YTGrantedAuthority(user));
    }

    public GrantedAuthority getAuthority() {
        return new YTGrantedAuthority(user);
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
