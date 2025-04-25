package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.InstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstructionRepository extends JpaRepository<InstructionEntity, String> {
    List<InstructionEntity> findByVideoId(String videoId);
}