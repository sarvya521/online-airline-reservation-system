package com.oars.web.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@ControllerAdvice
public class OarsExceptionHandler {
    @ExceptionHandler({Exception.class})
    public final ModelAndView handleExceptions(HttpServletRequest request, Exception ex) {
        log.error("<<Exceptions>>", ex);
        request.getSession().invalidate();
        log.info("forcefully logged out...");
        return new ModelAndView("error", "message", "unknown error");
    }
}
