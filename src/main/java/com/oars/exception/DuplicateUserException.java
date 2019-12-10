package com.oars.exception;

import com.oars.util.ErrorGenerator;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class DuplicateUserException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public DuplicateUserException(String code) {
        super(ErrorGenerator.generateForCode(code));
    }

}