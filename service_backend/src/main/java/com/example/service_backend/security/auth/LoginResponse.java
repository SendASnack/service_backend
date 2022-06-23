package com.example.service_backend.security.auth;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class LoginResponse {

    private String message;
    private String token;
    private Object user;

    public LoginResponse(){

    }
    
    public LoginResponse(String message, String token){
        this.message = message;
        this.token = token;
        this.user = null;
    }

    public LoginResponse(String message, String token, Object user){
        this.message = message;
        this.token = token;
        this.user = user;
    }
}
