package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.VideoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoMetadataRepository extends JpaRepository<VideoMetadata, String> {
}
