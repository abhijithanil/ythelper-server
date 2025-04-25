package com.inspire17.ythelper.controllers;

import com.inspire17.ythelper.dto.AccountInfoDto;
import com.inspire17.ythelper.dto.EditorDetailsDto;
import com.inspire17.ythelper.dto.EditorRequest;
import com.inspire17.ythelper.dto.UserRole;
import com.inspire17.ythelper.entity.VideoEditorEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.helper.Helper;
import com.inspire17.ythelper.service.EditorService;
import com.inspire17.ythelper.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/editor")
@RequiredArgsConstructor
public class EditorController {

    private final EditorService editorService;
    private final EmailService emailService;

    @GetMapping("/assigned")
    public ResponseEntity<EditorDetailsDto> getAssignedEditor(@RequestParam String videoId) {
        if (videoId == null) {
            throw new ServerException("Video id is not present", 400);
        }
        EditorDetailsDto editorForVideo = editorService.getEditorForVideo(videoId);
        return ResponseEntity.ok(editorForVideo);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EditorDetailsDto>> getAllEditor() {
        AccountInfoDto account = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        checkAdmin(account);
        return ResponseEntity.ok(editorService.getEditors());
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignEditor(@RequestBody EditorRequest request) {

        AccountInfoDto account = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        checkAdmin(account);
        editorService.assignEditor(request);
        emailService.sendAssignNotification(request);
        return ResponseEntity.ok("Editor assigned");
    }


    @DeleteMapping("/remove")
    public ResponseEntity<String> removeEditor(@RequestParam String videoId) {
        AccountInfoDto account = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        checkAdmin(account);
        VideoEditorEntity videoEditorEntity = editorService.removeEditor(videoId);

        emailService.sendUnAssignNotification(videoEditorEntity.getEditorId().getEmailId(), videoEditorEntity.getVideoId());
        return ResponseEntity.ok("Editor removed");
    }

    private void checkAdmin(AccountInfoDto account) {
        if (account.getUserRole() != UserRole.ADMIN) {
            throw new ServerException("Forbidden: Admins only", 403);
        }
    }

}
