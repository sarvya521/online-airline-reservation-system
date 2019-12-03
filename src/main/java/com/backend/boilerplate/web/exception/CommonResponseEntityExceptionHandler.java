package com.backend.boilerplate.web.exception;

import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.Response;
import com.backend.boilerplate.exception.ErrorDetails;
import com.backend.boilerplate.util.ErrorGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Set;

/**
 * Common exception handler to handle generic rest exceptions
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
@RestControllerAdvice
public class CommonResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                      HttpHeaders headers, HttpStatus status,
                                                                      WebRequest request) {
        log.error("<<HttpRequestMethodNotSupportedException>>", ex);
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1001"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
        log.error("<<NoHandlerFoundException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1003"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                       HttpHeaders headers, HttpStatus status,
                                                                       WebRequest request) {
        log.error("<<MissingServletRequestParameterException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1002"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                       HttpHeaders headers, HttpStatus status,
                                                                       WebRequest request) {
        log.error("<<ServletRequestBindingException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1002"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
                                                     WebRequest request) {
        log.error("<<TypeMismatchException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1002"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        log.error("<<HttpMessageNotReadableException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1002"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        log.error("<<MethodArgumentNotValidException>>", ex);
        Response errorResponse;
        if (ex.getBindingResult().hasFieldErrors()) {
            ErrorDetails[] arr =
                ex.getBindingResult().getFieldErrors()
                    .stream().map(error -> ErrorGenerator.generateForCode(error.getDefaultMessage()))
                    .toArray(ErrorDetails[]::new);
            errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(), arr);
        } else {
            errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(), ErrorGenerator.generateForCode(
                "1002"));
        }
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        log.error("<<MissingServletRequestPartException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1002"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                      WebRequest request) {
        log.error("<<BindException>>", ex);
        Response errorResponse = new Response<Void>(Status.CLIENT_ERROR, status.value(),
            ErrorGenerator.generateForCode("1002"
            ));
        return new ResponseEntity<>(errorResponse, headers, status);
    }
}
