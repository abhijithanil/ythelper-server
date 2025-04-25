package com.inspire17.ythelper.sec;

import com.inspire17.ythelper.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public final class YTGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 860L;
    private final String role;
    @Getter
    private final String accountStatus;

    public YTGrantedAuthority(UserEntity userEntity) {
        Assert.hasText(userEntity.getUserRole().getRole(), "A granted authority textual representation is required");
        this.role = userEntity.getUserRole().getRole();
        this.accountStatus = userEntity.getAccountStatus().name();
    }

    public String getAuthority() {
        return this.role;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof YTGrantedAuthority ytg) {
            return this.role.equals(ytg.getAuthority()) && this.accountStatus.equals(ytg.getAccountStatus());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.role.hashCode();
    }

    public String toString() {
        return this.role;
    }
}
