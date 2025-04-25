package com.inspire17.ythelper.dto;

import com.inspire17.ythelper.helper.annotations.ToJsonString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToJsonString
public class ChannelResponseDto extends BaseDto {
    private String id;
    private String name;
    private String youtubeId;
}
