package com.example.service_backend.security.auth;

import com.example.service_backend.utils.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        MessageResponse messageResponse = new MessageResponse(authException.getMessage());

        response.addHeader("Content-Type", "application/json");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(objectMapper.writeValueAsString(messageResponse));

    }

}