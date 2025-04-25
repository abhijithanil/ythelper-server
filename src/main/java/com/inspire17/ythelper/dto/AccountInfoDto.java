package com.inspire17.ythelper.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoDto {
    private UserRole userRole;
    private String name;
    private AccountStatus accountStatus;
}
