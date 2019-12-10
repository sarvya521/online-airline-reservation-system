package com.oars.exception;


import com.oars.util.ErrorGenerator;


public class DuplicateRoleException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public DuplicateRoleException() {
        super(ErrorGenerator.generateForCode("1008"));
    }

}
