package com.inspire17.ythelper.controllers;

import com.inspire17.ythelper.dto.AccountInfoDto;
import com.inspire17.ythelper.dto.ChannelResponseDto;
import com.inspire17.ythelper.helper.Helper;
import com.inspire17.ythelper.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/channel")
@Slf4j
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelResponseDto>> getAllChannels() {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        List<ChannelResponseDto> channelsForUser = channelService.getChannelForUser(accountInfo);
        return ResponseEntity.ok(channelsForUser);
    }


}
