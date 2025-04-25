package com.inspire17.ythelper.helper;

import com.inspire17.ythelper.dto.AccountInfoDto;
import com.inspire17.ythelper.dto.AccountStatus;
import com.inspire17.ythelper.dto.UserRole;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.sec.YTGrantedAuthority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.List;

@Slf4j
public class Helper {

    public static AccountInfoDto accountInfo(Authentication auth) {
        String username = auth.getName();
        List<? extends YTGrantedAuthority> authorities = (List<? extends YTGrantedAuthority>) auth.getAuthorities();
        if (authorities == null || authorities.size() == 0) {
            throw new ServerException("Failed to extract authorities, Login and try again", 500);
        }

        UserRole userRole = UserRole.fromString(authorities.getFirst().getAuthority());
        AccountStatus accountStatus = AccountStatus.fromString(authorities.getFirst().getAccountStatus());
        return AccountInfoDto.builder()
                .accountStatus(accountStatus)
                .userRole(userRole)
                .name(username)
                .build();
    }
}
