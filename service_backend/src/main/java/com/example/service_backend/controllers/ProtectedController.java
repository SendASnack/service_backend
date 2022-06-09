package com.example.service_backend.controllers;

import com.example.service_backend.services.UserService;
import com.example.service_backend.exception.implementations.BadRequestException;
import com.example.service_backend.model.User;
import com.example.service_backend.requests.ProfileRequest;
import com.example.service_backend.requests.AddressRequest;
import com.example.service_backend.requests.MessageResponse;

import java.security.Principal;
import java.time.Instant;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        return new ProfileRequest(res.getName(), res.getEmail(), res.getPhoneNumber());
    }

    @PostMapping("/profile/address")
    public MessageResponse updateAddress(HttpServletRequest request, @RequestBody AddressRequest addressRequest){
        Principal principal = request.getUserPrincipal();
        User res = userService.findByUsername(principal.getName());
        String city = addressRequest.getCity();
        String address = addressRequest.getAddress();
        String postalCode = addressRequest.getPostalCode();
        if (city == null || address == null || postalCode == null)
            throw new BadRequestException("Please provide a valid request body.");
        res.setCity(city);
        res.setAddress(address);
        res.setPostalCode(postalCode);
        userService.updateUserAdress(res);
        return new MessageResponse(Date.from(Instant.now()), "The user address was successfuly changed!");
    }
    
}
