package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.VideoEditorEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoEditorRepository extends JpaRepository<VideoEditorEntity, String> {
    Optional<VideoEditorEntity> findByVideoId(String videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoEditorEntity v WHERE v.videoId = :videoId")
    void deleteByVideoId(@Param("videoId") String videoId);
}
