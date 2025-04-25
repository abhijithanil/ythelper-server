package com.inspire17.ythelper.exceptions;

import com.inspire17.ythelper.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ResponseDto> handleServerRequestFailed(ServerException ex) {
        log.error("ServerRequestFailed Exception: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getCode())
                .body(new ResponseDto(ex.getMessage(), ex.getCode()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseDto> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("Requested resource not found: {}", ex.getRequestURL());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto("Error: The requested endpoint does not exist: " + ex.getRequestURL(), 404));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDto> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Requested resource not found: {}", ex.getResourcePath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto("Error: The requested endpoint does not exist: " + ex.getResourcePath(), 404));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        log.error("Unexpected Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDto("Internal Server Error", 500));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("File upload failed: Maximum upload size exceeded. Please upload a smaller file.");
    }
}
