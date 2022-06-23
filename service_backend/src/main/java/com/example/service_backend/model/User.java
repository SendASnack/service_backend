package com.example.service_backend.model;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    
    private Long id;

    private String username;

    private String password;

    private String name;

    private String phoneNumber;

    private String accountType;

}
