package com.oars.exception;

import com.oars.util.ErrorGenerator;


public class DuplicateUserException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public DuplicateUserException(String code) {
        super(ErrorGenerator.generateForCode(code));
    }

}
