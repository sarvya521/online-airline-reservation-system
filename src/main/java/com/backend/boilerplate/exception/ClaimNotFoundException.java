package com.backend.boilerplate.exception;

import com.backend.boilerplate.util.ErrorGenerator;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class ClaimNotFoundException extends UserManagementException {
    private static final long serialVersionUID = 1L;

    public ClaimNotFoundException() {
        super(ErrorGenerator.generateForCode("1015"));
    }

}
