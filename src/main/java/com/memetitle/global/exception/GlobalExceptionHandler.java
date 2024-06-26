package com.memetitle.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

import static com.memetitle.global.exception.ErrorCode.INVALID_REQUEST;
import static com.memetitle.global.exception.ErrorCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * @Valid 애노테이션으로 검증에 실패한 경우 발생
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        String errMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.info("[Response sent: MethodArgumentNotValidException - {}]", errMessage);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(INVALID_REQUEST.getCode(), errMessage));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.info("[Response sent: MaxUploadSizeExceededException - {}]", "파일 업로드 실패: 최대 업로드 크기를 초과했습니다");

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(INVALID_REQUEST.getCode(), "파일 업로드 실패: 최대 업로드 크기를 초과했습니다"));
    }

    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        log.info("[Response sent: AuthException - {}]", e.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidException(InvalidException e) {
        log.info("[Response sent: InvalidException - {}]", e.getMessage());

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("ERROR!! - {} {}", e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(SERVER_ERROR));
    }
}
