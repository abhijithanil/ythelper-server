package com.inspire17.ythelper.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoUploadResponseDto extends ResponseDto {
    private String videoId;

    public VideoUploadResponseDto(String message, int status, String videoId) {
        super(message, status);
        this.videoId = videoId;
    }

    public VideoUploadResponseDto(String message, int status) {
        super(message, status);
    }
}
