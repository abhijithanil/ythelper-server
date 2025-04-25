package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.InstructionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "yt_instructions")
@Getter
@Setter
public class InstructionEntity {
    @Id
    private String id = UUID.randomUUID().toString();

    private String videoId;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String text;
    private String audioUrl;
}