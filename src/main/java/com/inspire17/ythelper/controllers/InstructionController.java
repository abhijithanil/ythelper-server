package com.inspire17.ythelper.controllers;

import com.inspire17.ythelper.dto.AccountInfoDto;
import com.inspire17.ythelper.dto.InstructionResponseDto;
import com.inspire17.ythelper.dto.InstructionTextRequest;
import com.inspire17.ythelper.dto.UserRole;
import com.inspire17.ythelper.entity.InstructionEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.helper.Helper;
import com.inspire17.ythelper.service.InstructionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/instruction")
@RequiredArgsConstructor
public class InstructionController {

    private final InstructionService instructionService;

    // Fetch All Instructions (Text & Audio)
    @GetMapping("/all")
    public ResponseEntity<List<InstructionEntity>> getInstructions(@RequestParam String videoId) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() != UserRole.ADMIN){
            throw new ServerException("Unauthorised access", 403);
        }

        List<InstructionEntity> instructions = instructionService.getInstructions(videoId);
        return ResponseEntity.ok(instructions);
    }

    // Save Text Instruction
    @PostMapping("/text")
    public ResponseEntity<InstructionResponseDto> saveTextInstruction(@RequestBody InstructionTextRequest request) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() != UserRole.ADMIN){
            throw new ServerException("Unauthorised access", 403);
        }

        InstructionResponseDto instructionResponseDto = new InstructionResponseDto();
        instructionService.saveTextInstruction(request.getVideoId(), request.getText());
        instructionResponseDto.setMessage("Text instruction saved successfully");
        return ResponseEntity.ok(instructionResponseDto);
    }

    // Update Text Instruction
    @PutMapping("/text/{instructionId}")
    public ResponseEntity<InstructionResponseDto> updateTextInstruction(
            @PathVariable String instructionId,
            @RequestBody InstructionTextRequest request) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() != UserRole.ADMIN){
            throw new ServerException("Unauthorised access", 403);
        }

        instructionService.updateTextInstruction(instructionId, request.getText());
        InstructionResponseDto response = new InstructionResponseDto();
        response.setMessage("Text instruction updated successfully");
        return ResponseEntity.ok(response);
    }


    // Save Audio Instruction
    @PostMapping("/audio")
    public ResponseEntity<InstructionResponseDto> saveAudioInstruction(@RequestParam("videoId") String videoId,
                                                                       @RequestParam("audio") MultipartFile audioFile) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() != UserRole.ADMIN){
            throw new ServerException("Unauthorised access", 403);
        }

        InstructionResponseDto instructionResponseDto = new InstructionResponseDto();
        instructionService.saveAudioInstruction(videoId, audioFile);
        instructionResponseDto.setMessage("Audio instruction saved successfully");
        return ResponseEntity.ok(instructionResponseDto);
    }

    // Delete Instruction by ID
    @DeleteMapping("/{instructionId}")
    public ResponseEntity<InstructionResponseDto> deleteInstruction(@PathVariable String instructionId) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() != UserRole.ADMIN){
            throw new ServerException("Unauthorised access", 403);
        }
        
        InstructionResponseDto instructionResponseDto = new InstructionResponseDto();
        instructionService.deleteInstruction(instructionId);
        instructionResponseDto.setMessage("Instruction deleted successfully.");
        return ResponseEntity.ok(instructionResponseDto);
    }


}
