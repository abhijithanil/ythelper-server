package com.inspire17.ythelper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String username;
    private String emailId;
    private String password;
}
