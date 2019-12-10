package com.oars.exception;

import com.oars.util.ErrorGenerator;


public class RoleNotFoundException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public RoleNotFoundException() {
        super(ErrorGenerator.generateForCode("1009"));
    }

}
