package com.shoplane.common;

import org.springframework.http.HttpStatus;

/**
 * Domain-level exception. Controllers/services throw this to produce a
 * structured error response with a stable code and HTTP status.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final Object details;

    public ApiException(HttpStatus status, String code, String message, Object details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details;
    }

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, null);
    }

    public HttpStatus getStatus() { return status; }
    public String getCode()       { return code; }
    public Object getDetails()    { return details; }

    // ---- factory helpers ------------------------------------------------
    public static ApiException badRequest(String msg)   { return new ApiException(HttpStatus.BAD_REQUEST,   "BAD_REQUEST",   msg); }
    public static ApiException unauthorized(String msg) { return new ApiException(HttpStatus.UNAUTHORIZED,  "UNAUTHORIZED",  msg); }
    public static ApiException forbidden(String msg)    { return new ApiException(HttpStatus.FORBIDDEN,     "FORBIDDEN",     msg); }
    public static ApiException notFound(String msg)     { return new ApiException(HttpStatus.NOT_FOUND,     "NOT_FOUND",     msg); }
    public static ApiException conflict(String msg)     { return new ApiException(HttpStatus.CONFLICT,      "CONFLICT",      msg); }
    public static ApiException unprocessable(String msg, Object details) {
        return new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE", msg, details);
    }
}
