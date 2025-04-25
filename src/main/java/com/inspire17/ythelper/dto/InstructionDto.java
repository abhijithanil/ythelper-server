package com.inspire17.ythelper.dto;

import com.inspire17.ythelper.helper.annotations.ToJsonString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToJsonString
public class InstructionDto extends BaseDto {
    private String instructionId;
    private String videoId;
    private InstructionType type;
    private String content;
}