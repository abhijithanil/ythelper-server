package com.inspire17.ythelper.service;

import com.inspire17.ythelper.dto.EditorDetailsDto;
import com.inspire17.ythelper.dto.EditorRequest;
import com.inspire17.ythelper.dto.UserWrapperDto;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.entity.VideoEditorEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.repository.VideoEditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EditorService {

    private final VideoEditorRepository videoEditorRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;

    public EditorDetailsDto getEditorForVideo(String videoId) {
        EditorDetailsDto editorDetailsDto = new EditorDetailsDto();

        Optional<VideoEditorEntity> videoEditorEntityObj = videoEditorRepository.findByVideoId(videoId);
        if (videoEditorEntityObj.isEmpty()){
            return editorDetailsDto;
        }
        VideoEditorEntity videoEditorEntity = videoEditorEntityObj.get();
        editorDetailsDto.setId(videoEditorEntity.getEditorId().getId());
        editorDetailsDto.setEmail(videoEditorEntity.getEditorId().getEmailId());
        editorDetailsDto.setUserName(videoEditorEntity.getEditorId().getUsername());
        editorDetailsDto.setName(videoEditorEntity.getEditorId().getFullName());
        return editorDetailsDto;
    }

    public void assignEditor(EditorRequest editorRequest) {
        Optional<UserEntity> userEntityObj = userDetailsService.findUserByName(editorRequest.getEditorUserName());
        if (userEntityObj.isEmpty()) {
            throw new ServerException("Failed to validate user", 400);
        }

        Optional<VideoEditorEntity> existingAssignment = videoEditorRepository.findByVideoId(editorRequest.getVideoId());

        VideoEditorEntity videoEditorEntity;
        if (existingAssignment.isPresent()) {
            videoEditorEntity = existingAssignment.get();
            videoEditorEntity.setEditorId(userEntityObj.get());
        } else {
            videoEditorEntity = new VideoEditorEntity();
            videoEditorEntity.setVideoId(editorRequest.getVideoId());
            videoEditorEntity.setEditorId(userEntityObj.get());
        }

        videoEditorRepository.save(videoEditorEntity);
    }


    public VideoEditorEntity removeEditor(String videoId) {
        Optional<VideoEditorEntity> videoEditorEntityOpt = videoEditorRepository.findByVideoId(videoId);
        if (videoEditorEntityOpt.isPresent()) {
            videoEditorRepository.deleteByVideoId(videoId);
        }
        return videoEditorEntityOpt.get();
    }

    public List<EditorDetailsDto> getEditors() {
        return userDetailsService.getAllEditors();
    }
}
