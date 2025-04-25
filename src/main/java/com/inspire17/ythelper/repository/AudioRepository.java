package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.entity.AudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRepository extends JpaRepository<AudioEntity, String> {

}
