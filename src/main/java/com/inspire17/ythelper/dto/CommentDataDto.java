package com.inspire17.ythelper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDataDto {
    private String type; // "TEXT" or "AUDIO"
    private String content; // If text, stores text. If audio, stores URL/path to file.
}