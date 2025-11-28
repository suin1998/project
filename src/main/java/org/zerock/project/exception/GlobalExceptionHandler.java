package org.zerock.project.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.zerock.project.dto.ErrorResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
        // favicon.ico 요청은 무시
        if (ex.getMessage().contains("favicon.ico")) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        log.error("Resource not found: ", ex);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.error("Validation error: ", ex);

        List<ErrorResponseDTO.ValidationError> validationErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(ErrorResponseDTO.ValidationError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .build());
        });

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(
                "입력값 검증에 실패했습니다.",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex,
            HttpServletRequest request) {

        log.error("User already exists: ", ex);

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDTO);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(
            RuntimeException ex,
            HttpServletRequest request) {

        log.error("Invalid credentials: ", ex);

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(
                "이메일 또는 비밀번호가 올바르지 않습니다.",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        log.error("User not found: ", ex);

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(
                "사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error: ", ex);

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(
                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDTO);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.error("Illegal argument: ", ex);

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.of(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }
}
