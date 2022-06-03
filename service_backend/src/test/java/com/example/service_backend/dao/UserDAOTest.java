package com.example.service_backend.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.service_backend.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Test
    void toDataEntity() {

        UserDAO userDAO = new UserDAO("Hugo1307", "hugogoncalves13@ua.pt", "12345", "Hugo", "919312945");
        User user = new User(userDAO.getUsername(), userDAO.getEmail(), userDAO.getPassword(), userDAO.getName(), userDAO.getPhoneNumber());

        assertThat(userDAO.toDataEntity()).isEqualTo(user);

    }

}