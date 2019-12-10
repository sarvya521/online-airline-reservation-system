package com.oars.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


public class FeatureNotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final String ERROR = "Feature not yet implemented";

    /**
     * see {@link ErrorDetails}
     */
    @Getter
    private final ErrorDetails error;


    /**
     * @see RuntimeException#RuntimeException()
     */
    public FeatureNotImplementedException() {
        this.error = new ErrorDetails(String.valueOf(HttpStatus.NOT_IMPLEMENTED.value()), ERROR);
    }

}
