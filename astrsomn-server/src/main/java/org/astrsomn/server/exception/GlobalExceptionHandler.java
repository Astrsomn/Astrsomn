package org.astrsomn.server.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleServerBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        int code = ex.getCode() != null ? ex.getCode() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        log.warn("业务异常 - URI: {}, code={}, message={}", request.getRequestURI(), code, ex.getMessage(), ex);
        return ResponseEntity.status(status).body(buildBody(code, ex.getMessage(), ex, request));
    }

    @ExceptionHandler(org.astrsomn.core.exception.base.BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleCoreBusinessException(
            org.astrsomn.core.exception.base.BusinessException ex,
            HttpServletRequest request) {
        int code = ex.getCode();
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        log.warn("核心业务异常 - URI: {}, code={}, message={}", request.getRequestURI(), code, ex.getMessage(), ex);
        return ResponseEntity.status(status).body(buildBody(code, ex.getMessage(), ex, request));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            Exception ex,
            HttpServletRequest request) {
        String message = resolveMessage(ex);
        log.warn("请求参数异常 - URI: {}, message={}", request.getRequestURI(), message, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST.value(), message, ex, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex,
            HttpServletRequest request) {
        log.error("未捕获异常 - URI: {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误", ex, request));
    }

    private Map<String, Object> buildBody(int code, String message, Throwable ex, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("success", false);
        body.put("path", request.getRequestURI());
        body.put("exception", ex.getClass().getName());
        body.put("timestamp", Instant.now().toString());
        body.put("rootCause", resolveRootCause(ex));
        return body;
    }

    private String resolveMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException manve && manve.getBindingResult().hasFieldErrors()) {
            return manve.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        if (ex instanceof BindException be && be.getBindingResult().hasFieldErrors()) {
            return be.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        String message = ex.getMessage();
        return message == null || message.isBlank() ? "参数错误" : message;
    }

    private String resolveRootCause(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage();
    }
}
