package com.epam.esm.entity;

public enum ErrorCode {
    RESOURCE_NOT_FOUND(40401),
    TYPE_MISMATCH(40005),
    HTTP_MESSAGE_NOT_READABLE(40010),
    METHOD_ARGUMENT_NOT_VALID(40015),
    PATH_VARIABLE_NOT_VALID(40020),
    USER_CREDENTIALS_NOT_VALID(40025),
    USERNAME_NOT_FOUND(40030),
    UNIQUE_ENTITY_EXISTS_ERROR(40035),
    AUTH_ERROR(40101),
    ACCESS_ERROR(40301),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(40525),
    NO_HANDLER_FOUND(40430),
    COMMON_ERROR(40099);

    int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
