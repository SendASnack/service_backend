package com.example.service_backend.controllers;

import com.example.service_backend.model.BusinessUserUtil;

import org.json.JSONObject;
import org.springframework.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.service_backend.dao.UserDAO;
import com.example.service_backend.exception.implementations.BadRequestException;
import com.example.service_backend.model.Cart;
import com.example.service_backend.model.Costumer;
import com.example.service_backend.security.auth.AuthTokenResponse;
import com.example.service_backend.security.auth.JWTTokenUtils;
import com.example.service_backend.security.auth.LoginResponse;
import com.example.service_backend.services.SpringUserDetailsService;
import com.example.service_backend.utils.LoginRequest;
import com.example.service_backend.utils.MessageResponse;
import com.example.service_backend.services.CostumerService;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final CostumerService userService;
    private final SpringUserDetailsService springUserDetailsService;
    private final JWTTokenUtils jwtTokenUtils;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, CostumerService userService, SpringUserDetailsService springUserDetailsService, JWTTokenUtils jwtTokenUtils) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.springUserDetailsService = springUserDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("/register")
    public MessageResponse registerUser(@RequestBody UserDAO userDAO) {

        // Encode password
        String userPassword = userDAO.getPassword();
        userDAO.setPassword(passwordEncoder.encode(userPassword));

        Costumer user = userDAO.toDataEntity();

        Cart cart = new Cart();
        cart.setCostumer(user);

        user.setC(cart);

        userService.registerUser(user);

        return new MessageResponse("The user was successfully registered!");

    }

    @PostMapping("/login")
    public AuthTokenResponse loginUser(@RequestBody LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (email == null || password == null)
            throw new BadRequestException("Please provide a valid request body.");

        Costumer user = userService.findByEmail(email);
        UserDetails userDetails = springUserDetailsService.loadUserByUsername(user.getUsername());

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("The provided password is wrong.");

        String businessEmail = "business_sendasnack@ua.pt";
        String businessPassword = "send_a_snack_business_pass";
        
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject businessAuth = new JSONObject();
        businessAuth.put("email", businessEmail).put("password", businessPassword);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(businessAuth.toString(),httpHeaders);

        ResponseEntity<LoginResponse> response = template.exchange("http://20.77.90.223:8080/api/auth/login",HttpMethod.POST, entity, LoginResponse.class);

        BusinessUserUtil.businessToken = response.getBody().getToken();
        
        /* 
        Optional<Cart> cartOptional = cartService.findById(user.getId());

        if (!cartOptional.isPresent()){
            Cart cart = new Cart(user.getId(), user);
            cartService.registerCart(cart);
        }*/

        String token = jwtTokenUtils.generateToken(userDetails);
        return new AuthTokenResponse("Authentication succeeded.", token);

    }

}