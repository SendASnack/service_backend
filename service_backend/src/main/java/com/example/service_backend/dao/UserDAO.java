package com.example.service_backend.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import com.example.service_backend.model.Costumer;
import com.example.service_backend.model.Address;

@Data
@AllArgsConstructor
@Generated
public class UserDAO implements IEntityDAO<Costumer> {

    private String username;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private Address address;

    @Override
    public Costumer toDataEntity() {
        return new Costumer(username, email, password, name, phoneNumber, address);
    }

}