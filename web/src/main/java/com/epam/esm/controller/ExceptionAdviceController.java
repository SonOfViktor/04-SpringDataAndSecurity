package com.epam.esm.controller;

import com.epam.esm.entity.ErrorCode;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Order
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdviceController {
    private static final String RESOURCE_NOT_FOUND = "resource_not_found";
    private static final String ARGUMENT_NOT_VALID = "argument_not_valid";
    private static final String TYPE_MISMATCH = "type_mismatch";
    private static final String JSON_CONVERSION_FAILURE = "json_conversion_failure";
    private static final String INVALID_URL = "invalid_url";
    private static final String UNSUPPORTED_METHOD = "unsupported_method";
    public static final String UNIQUE_ENTITY_EXISTS = "unique_entity_exists";
    private static final String GLOBAL_EXCEPTION = "global_exception";
    private static final String METHOD_PARAMETER_NOT_VALID = "method_parameter_not_valid";
    private static final String EXCEPTION_MESSAGE_MAP_KEY = "exception_message";

    private final MessageSource messageSource;

    @ExceptionHandler({ResourceNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorInfo> resourceNotFoundExceptionHandler(Exception ex, Locale locale) {
        String errorMessage = messageSource.getMessage(RESOURCE_NOT_FOUND, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.RESOURCE_NOT_FOUND.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> typeMismatchExceptionHandler(TypeMismatchException ex, Locale locale) {
        String errorMessage = messageSource
                .getMessage(TYPE_MISMATCH, new Object[]{ex.getRequiredType(), ex.getValue()}, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.TYPE_MISMATCH.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> httpMessageNotReadableExceptionHandler
            (HttpMessageNotReadableException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(JSON_CONVERSION_FAILURE, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.HTTP_MESSAGE_NOT_READABLE.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> methodArgumentNotValidExceptionHandler
            (MethodArgumentNotValidException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(ARGUMENT_NOT_VALID, new Object[]{ex.getFieldErrorCount()}, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex, locale),
                ErrorCode.METHOD_ARGUMENT_NOT_VALID.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> pathVariableNotValidException(ConstraintViolationException ex, Locale locale) {
        String errorMessage = messageSource
                .getMessage(METHOD_PARAMETER_NOT_VALID, takeArgsForMethodParameterNotValidCode(ex), locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.PATH_VARIABLE_NOT_VALID.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> httpRequestMethodNotSupportedExceptionHandler
            (HttpRequestMethodNotSupportedException ex, Locale locale) {
        String errorMessage = messageSource
                .getMessage(UNSUPPORTED_METHOD, new Object[]{ex.getMethod(), ex.getSupportedHttpMethods()}, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> noHandlerFoundExceptionHandler(NoHandlerFoundException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(INVALID_URL, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.NO_HANDLER_FOUND.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> entityExistsExceptionHandler(EntityExistsException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(UNIQUE_ENTITY_EXISTS, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.UNIQUE_ENTITY_EXISTS_ERROR.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorInfo> commonExceptionHandler(Exception ex, Locale locale) {
        String errorMessage = messageSource.getMessage(GLOBAL_EXCEPTION, null, locale);
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, makeClarifiedDataMap(ex),
                ErrorCode.COMMON_ERROR.getCode());

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> makeClarifiedDataMap(Exception ex) {
        Map<String, String> clarifiedDataMap = new HashMap<>();
        clarifiedDataMap.put(EXCEPTION_MESSAGE_MAP_KEY, ex.getMessage());

        return clarifiedDataMap;
    }

    private Map<String, String> makeClarifiedDataMap(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, String> clarifiedDataMap = ex.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                            error -> messageSource.getMessage(error,  locale),
                            (value1, value2) -> String.join(", ", value1, value2)));

        clarifiedDataMap.putAll(makeClarifiedDataMap(ex));

        return clarifiedDataMap;
    }

    private Object[] takeArgsForMethodParameterNotValidCode(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(cv -> List.of(
                        cv.getInvalidValue(),
                        cv.getPropertyPath().iterator().next().getName(),
                        cv.getRootBeanClass().getSimpleName())
                )
                .map(List::toArray)
                .findFirst()
                .orElse(new Object[]{});
    }
}
