package com.inspire17.ythelper.controllers;

import com.inspire17.ythelper.dto.VideoStatusDto;
import com.inspire17.ythelper.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/op")
@Slf4j
public class OperationsController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/video_status")
    public VideoStatusDto getVideoStatus(@RequestParam("videoId") String videoId) {
        return videoService.getVideoStatus(videoId);
    }

}
