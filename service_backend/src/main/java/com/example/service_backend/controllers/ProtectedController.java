package com.example.service_backend.controllers;

import com.example.service_backend.services.UserService;
import com.example.service_backend.model.User;
import com.example.service_backend.requests.ProfileRequest;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProtectedController {

    private final UserService userService;

    @Autowired
    public ProtectedController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ProfileRequest getProfile(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        User res = userService.findByUsername(principal.getName());
        return new ProfileRequest(res.getName(), res.getEmail());
    }

    
}
