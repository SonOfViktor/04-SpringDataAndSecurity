package com.epam.esm.controller;

import com.epam.esm.entity.ErrorCode;
import com.epam.esm.entity.ErrorInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Order(0)
@ControllerAdvice
@RequiredArgsConstructor
public class SecurityExceptionAdviceController {
    public static final String SECURITY_ACCESS_DENIED = "security.access_denied";
    public static final String SECURITY_AUTHENTICATION_ERROR = "security.authentication_error";
    private static final String BAD_CREDENTIALS_ERROR = "security.bad_credentials_error";
    private static final String USERNAME_NOT_FOUND_ERROR = "security.username_not_found";
    private static final String EXCEPTION_MESSAGE_MAP_KEY = "exception_message";

    private final MessageSource messageSource;

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> accessDeniedExceptionHandler(AccessDeniedException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(SECURITY_ACCESS_DENIED, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex), ErrorCode.ACCESS_ERROR.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> insufficientAuthenticationExceptionHandler(InsufficientAuthenticationException ex,
                                                                                Locale locale) {
        String errorMessage = messageSource.getMessage(SECURITY_AUTHENTICATION_ERROR, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex), ErrorCode.AUTH_ERROR.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> badCredentialsExceptingHandler(BadCredentialsException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(BAD_CREDENTIALS_ERROR, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.USER_CREDENTIALS_NOT_VALID.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> usernameNotFoundException(UsernameNotFoundException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(USERNAME_NOT_FOUND_ERROR, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.USERNAME_NOT_FOUND.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    private Map<String, String> makeClarifiedDataMap(Exception ex) {
        Map<String, String> clarifiedDataMap = new HashMap<>();
        clarifiedDataMap.put(EXCEPTION_MESSAGE_MAP_KEY, ex.getMessage());

        return clarifiedDataMap;
    }
}
