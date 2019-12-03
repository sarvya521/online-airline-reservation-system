package com.backend.boilerplate.web.exception;

import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.Response;
import com.backend.boilerplate.exception.ErrorDetails;
import com.backend.boilerplate.exception.UserManagementException;
import com.backend.boilerplate.util.ErrorGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

/**
 * Exception handler to handle API specific exceptions.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
@RestControllerAdvice
public class UserManagementExceptionHandler extends CommonResponseEntityExceptionHandler {

    @SuppressWarnings({"rawtypes"})
    @ExceptionHandler({DataIntegrityViolationException.class})
    public final ResponseEntity<Response> handleDataIntegrityViolationException(DataIntegrityViolationException dex,
                                                                                WebRequest request) {
        Response<Void> errorResponse = new Response<Void>(Status.CLIENT_ERROR, HttpStatus.BAD_REQUEST.value(),
            ErrorGenerator.generateForCode("1000"));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings({"rawtypes"})
    @ExceptionHandler({ConstraintViolationException.class})
    public final ResponseEntity<Response> handleConstraintViolationException(ConstraintViolationException ax,
                                                                             WebRequest request) {
        log.error("<<ConstraintViolationException>>", ax);
        ErrorDetails[] arr =
            ax.getConstraintViolations().stream()
                .map(error -> ErrorGenerator.generateForCode(error.getMessage()))
                .toArray(ErrorDetails[]::new);
        Response<Void> errorResponse = new Response<Void>(Status.CLIENT_ERROR, HttpStatus.BAD_REQUEST.value(), arr);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings({"rawtypes"})
    @ExceptionHandler({UserManagementException.class})
    public final ResponseEntity<Response> handleUserManagementException(UserManagementException ax,
                                                                        WebRequest request) {
        log.error("<<Exceptions>>", ax);
        Response errorResponse = new Response<Void>(Status.FAIL, HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ax.getError());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings({"rawtypes"})
    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Response> handleExceptions(Exception ex, WebRequest request) {
        log.error("<<Exceptions>>", ex);
        Response errorResponse = new Response<Void>(Status.FAIL, HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ErrorGenerator.generateForCode("1000"));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
