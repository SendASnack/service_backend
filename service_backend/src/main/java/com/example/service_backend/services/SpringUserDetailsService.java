package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

import com.example.service_backend.model.Costumer;

//import java.util.Collections;

@Service
public class SpringUserDetailsService implements UserDetailsService {

    private final CostumerService userService;

    @Autowired
    public SpringUserDetailsService(CostumerService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Costumer user = userService.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }

}