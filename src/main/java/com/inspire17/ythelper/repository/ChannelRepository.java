package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.ChannelEntity;
import com.inspire17.ythelper.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Optional<ChannelEntity> findById(Long channelId);

    Optional<List<ChannelEntity>> findByAdmin(UserEntity userEntity);
}
