package com.oars.exception;

import com.oars.util.ErrorGenerator;


public class UserNotFoundException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super(ErrorGenerator.generateForCode("1004"));
    }
}
