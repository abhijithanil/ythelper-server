package com.inspire17.ythelper.service;

import com.inspire17.ythelper.dto.AccountInfoDto;
import com.inspire17.ythelper.dto.ChannelResponseDto;
import com.inspire17.ythelper.entity.ChannelEntity;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.repository.ChannelRepository;
import com.inspire17.ythelper.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;


    public List<ChannelResponseDto> getChannelForUser(AccountInfoDto accountInfoDto) {

        Optional<UserEntity> userEntity = userRepository.findByUsername(accountInfoDto.getName());
        if (userEntity.isEmpty()) {
            throw new ServerException("User doesn't exist", 403);
        }
        Optional<List<ChannelEntity>> channels = channelRepository.findByAdmin(userEntity.get());
        List<ChannelResponseDto> channelResponseDtoList = new ArrayList<>();
        channels.ifPresent(channelEntities -> channelEntities.forEach(e -> {
            ChannelResponseDto channelResponseDto = new ChannelResponseDto();
            channelResponseDto.setName(e.getChannelName());
            channelResponseDto.setId(String.valueOf(e.getId()));
            channelResponseDtoList.add(channelResponseDto);
        }));

        return channelResponseDtoList;

    }
}
