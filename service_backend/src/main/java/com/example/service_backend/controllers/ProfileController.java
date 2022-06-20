package com.example.service_backend.controllers;

import com.example.service_backend.services.CostumerService;
import com.example.service_backend.exception.implementations.BadRequestException;
import com.example.service_backend.model.Costumer;
import com.example.service_backend.requests.ProfileRequest;
import com.example.service_backend.security.auth.AuthHandler;
import com.example.service_backend.requests.AddressRequest;
import com.example.service_backend.requests.PaymentRequest;
import com.example.service_backend.requests.MessageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final AuthHandler authHandler;
    private final CostumerService userService;

    @Autowired
    public ProfileController(CostumerService userService, AuthHandler authHandler) {
        this.userService = userService;
        this.authHandler = authHandler;
    }

    @GetMapping("/profile")
    public ProfileRequest getProfile() {
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());
        return new ProfileRequest(res.getName(), res.getEmail(), res.getPhoneNumber());
    }

    @PostMapping("/profile/address")
    public MessageResponse updateAddress(@RequestBody AddressRequest addressRequest){
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());
        String city = addressRequest.getCity();
        String address = addressRequest.getAddress();
        String postalCode = addressRequest.getPostalCode();
        if (city == null || address == null || postalCode == null)
            throw new BadRequestException("Please provide a valid request body.");

        res.getAddress().setCity(city);
        res.getAddress().setPostalCode(postalCode);
        res.getAddress().setStreet(address);
        userService.updateUser(res);
        return new MessageResponse("The user information was successfuly changed!");
    }
    
    @PostMapping("/profile/payment")
    public MessageResponse updatePayment(@RequestBody PaymentRequest paymentRequest){
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());
        String name = paymentRequest.getName();
        String cardNumber = paymentRequest.getCardNumber();
        String cardType = paymentRequest.getCardType();
        String cvv = paymentRequest.getCvv();
        if (name == null || cardNumber == null || cardType == null || cvv == null)
            throw new BadRequestException("Please provide a valid request body.");
        res.setCardName(name);
        res.setCardNumber(cardNumber);
        res.setCardType(cardType);
        res.setCvv(cvv);
        userService.updateUser(res);
        return new MessageResponse("The user information was successfuly changed!");
    }
}
