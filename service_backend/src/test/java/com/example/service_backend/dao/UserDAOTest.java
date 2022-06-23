package com.example.service_backend.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.service_backend.model.Address;
import com.example.service_backend.model.Costumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Test
    void toDataEntity() {
        Address address = new Address("city", "street", "postalCode");
        UserDAO userDAO = new UserDAO("Hugo1307", "hugogoncalves13@ua.pt", "12345", "Hugo", "919312945", address);
        Costumer user = new Costumer(userDAO.getUsername(), userDAO.getEmail(), userDAO.getPassword(), userDAO.getName(), userDAO.getPhoneNumber(), userDAO.getAddress());

        assertThat(userDAO.toDataEntity()).isEqualTo(user);

    }

}