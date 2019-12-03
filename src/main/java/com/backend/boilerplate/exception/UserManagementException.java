package com.backend.boilerplate.exception;

import lombok.Getter;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class UserManagementException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * see {@link ErrorDetails}
     */
    @Getter
    private final ErrorDetails error;


    /**
     * @param error OwError
     * @see RuntimeException#RuntimeException(String)
     */
    public UserManagementException(ErrorDetails error) {
        super(error.toString());
        this.error = error;
    }

    /**
     * @param error OwError
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public UserManagementException(ErrorDetails error, Throwable cause) {
        super(error.toString(), cause);
        this.error = error;
    }
}
