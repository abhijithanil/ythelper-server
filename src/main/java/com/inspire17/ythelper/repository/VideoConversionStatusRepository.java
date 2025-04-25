package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.VideoConversionStatusEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VideoConversionStatusRepository extends JpaRepository<VideoConversionStatusEntity, String> {
    @Modifying
    @Transactional
    @Query("UPDATE VideoConversionStatusEntity v SET v.status = :status WHERE v.video.id = :videoId")
    void updateStatus(@Param("videoId") String videoId, @Param("status") boolean status);

    Optional<VideoConversionStatusEntity> findByVideoId(String videoId);
}
