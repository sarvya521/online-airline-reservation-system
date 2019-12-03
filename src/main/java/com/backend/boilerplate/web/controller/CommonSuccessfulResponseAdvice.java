package com.backend.boilerplate.web.controller;

import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * This ResponseBodyAdvice act as a wrapper for final response into {@link Response} ONLY in case of SUCCESSFUL API
 * CALL.
 * No need to write code for every endpoint at controller layer to create final {@code Response} object.
 * But we lose swagger documentation for final {@code Response}
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@ControllerAdvice
public class CommonSuccessfulResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * {@inheritDoc}
     * Only applicable for RestControllers written inside specific package.
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //return returnType.getDeclaringClass().getPackageName().equals("com.roche.navify.ow.web.controller"); //JAVA
        // 9 >
        return returnType.getDeclaringClass().getPackage().getName().equals("com.roche.navify.ow.web.controller");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof Response) {// defensive
            return body;
        }
        return new Response<>(Status.SUCCESS, ((ServletServerHttpResponse) response).getServletResponse().getStatus()
            , body);
    }

}
