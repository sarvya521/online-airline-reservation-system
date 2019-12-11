package com.oars.web.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@ControllerAdvice
public class OarsExceptionHandler {
    @ExceptionHandler({Exception.class})
    public final ModelAndView handleExceptions(Exception ex, WebRequest request) {
        log.error("<<Exceptions>>", ex);
        return new ModelAndView("error", "message", "unknown error");
    }
}
