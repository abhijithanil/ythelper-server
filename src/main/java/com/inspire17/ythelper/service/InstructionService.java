package com.inspire17.ythelper.service;

import com.inspire17.ythelper.entity.InstructionEntity;
import com.inspire17.ythelper.repository.InstructionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructionService {

    private final InstructionRepository instructionRepository;
    private final FileStorageService fileStorageService;  // For audio saving

    public List<InstructionEntity> getInstructions(String videoId) {
        return instructionRepository.findByVideoId(videoId);
    }

    public void saveTextInstruction(String videoId, String text) {
        InstructionEntity entity = new InstructionEntity();
        entity.setVideoId(videoId);
        entity.setType("TEXT");
        entity.setText(text);
        instructionRepository.save(entity);
    }

    public void saveAudioInstruction(String videoId, MultipartFile audioFile) {
        String audioUrl = fileStorageService.storeAudio(audioFile, "audio_instructions/");
        InstructionEntity entity = new InstructionEntity();
        entity.setVideoId(videoId);
        entity.setType("AUDIO");
        entity.setAudioUrl(audioUrl);
        instructionRepository.save(entity);
    }

    @Transactional
    public void deleteInstruction(String instructionId) {
        instructionRepository.deleteById(instructionId);
    }

    @Transactional
    public void updateTextInstruction(String instructionId, String updatedText) {
        InstructionEntity instruction = instructionRepository.findById(instructionId)
                .orElseThrow(() -> new RuntimeException("Instruction not found"));
        instruction.setText(updatedText);
        instructionRepository.save(instruction);
    }
}
