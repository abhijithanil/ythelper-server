package com.inspire17.ythelper.dto;

import com.inspire17.ythelper.helper.annotations.ToJsonString;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@ToJsonString
@Getter
@Setter
public class VideoDto extends BaseDto {
    private String id;
    private String title;
    private String description;
    private Integer revisionId;
    private Long channelId;
    private String channelName;
    private String uploadedBy;
    private VideoStatus status;
    private LocalDateTime uploadedAt;
    private String filePath;
    private String thumbnail;
    private String assignee = "None";
}
