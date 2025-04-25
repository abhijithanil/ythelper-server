package com.inspire17.ythelper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditorRequest {
    // DTO for assignment
   private String videoId;
   private String editorId;
   private String editorEmail;
   private String editorUserName;
}
