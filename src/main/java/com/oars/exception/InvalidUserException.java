package com.oars.exception;

import com.oars.util.ErrorGenerator;


public class InvalidUserException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public InvalidUserException() {
        super(ErrorGenerator.generateForCode("1006"));
    }
}
