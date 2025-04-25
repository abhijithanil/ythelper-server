package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.ChannelEntity;
import com.inspire17.ythelper.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, String> {
    List<VideoEntity> findByChannel(ChannelEntity channel);

    // Custom query to fetch the 0th revision
    @Query("SELECT v FROM VideoEntity v WHERE v.channel = :channel AND v.revisionId = 1")
    List<VideoEntity> find0thRevisionByChannel(@Param("channel") ChannelEntity channel);

    List<VideoEntity> findByParentId(VideoEntity parent);

    @Query("SELECT COALESCE(MAX(v.revisionId), 0) FROM VideoEntity v WHERE v.parentId.id = :parentId")
    int findLatestRevisionNumber(@Param("parentId") String parentId);

}
