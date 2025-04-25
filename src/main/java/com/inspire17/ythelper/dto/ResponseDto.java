package com.inspire17.ythelper.dto;

import com.inspire17.ythelper.helper.annotations.ToJsonString;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ToJsonString
public class ResponseDto extends BaseDto {
    private String message;
    private int status;
}
