package com.example.service_backend.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import com.example.service_backend.model.User;

@Data
@AllArgsConstructor
@Generated
public class UserDAO implements IEntityDAO<User> {

    private String username;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    @Override
    public User toDataEntity() {
        return new User(username, email, password, name, phoneNumber);
    }

}