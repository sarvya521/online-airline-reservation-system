package com.backend.boilerplate.exception;

import com.backend.boilerplate.util.ErrorGenerator;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class InvalidUserException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public InvalidUserException() {
        super(ErrorGenerator.generateForCode("1006"));
    }
}
