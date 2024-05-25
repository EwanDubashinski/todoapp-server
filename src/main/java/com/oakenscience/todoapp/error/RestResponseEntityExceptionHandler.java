package com.oakenscience.todoapp.error;

import com.oakenscience.todoapp.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import io.jsonwebtoken.MalformedJwtException;

import java.util.Locale;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private MessageSource messages;


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("400 Status Code", ex);
        BindingResult result = ex.getBindingResult();
        GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid " + result.getObjectName());
        return handleExceptionInternal(ex, bodyOfResponse, headers, status, request);
    }

//    @Override
//    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        logger.error("400 Status Code", ex);
//        BindingResult result = ex.getBindingResult();
//        final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
//        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
////        return super.handleBindException(ex, headers, status, request);
//    }

    // 409
    @ExceptionHandler({ UserAlreadyExistException.class })
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex, final WebRequest request) {
        logger.error("409 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExist");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({ UserActivationFailed.class })
    public ResponseEntity<Object> handleActivationFailed(final RuntimeException ex, final WebRequest request) {
        logger.error("403 Status Code", ex);
        Locale locale = request.getLocale();
        String message = messages.getMessage("message.actError", null, locale);
        final GenericResponse bodyOfResponse = new GenericResponse(message, "ActivationFailed");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({MalformedJwtException.class})
    public ResponseEntity<Object> handleMalformedJwtException(final RuntimeException ex, final WebRequest request) {
        logger.error("401 Status Code", ex);
        Locale locale = request.getLocale();
        String message = messages.getMessage("message.badToken", null, locale);
        final GenericResponse bodyOfResponse = new GenericResponse(message, "Bad token");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
}
