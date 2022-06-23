package com.example.service_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.service_backend.services.SpringUserDetailsService;
import com.example.service_backend.services.CostumerService;
import com.example.service_backend.model.Address;
import com.example.service_backend.model.Costumer;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringUserDetailsServiceTest {

    @Mock
    private CostumerService userService;

    @InjectMocks
    private SpringUserDetailsService springUserDetailsService;

    private UserDetails userDetails;
    private Costumer user;

    @BeforeEach
    void setUp() {
        Address address = new Address("city", "street", "postalCode");
        user = new Costumer("Hugo1307", "hugogoncalves13@ua.pt", "12345", "Hugo", "919312945", address);
        userDetails = new org.springframework.security.core.userdetails.User("Hugo1307", "123456", Collections.emptyList());
    }

    @Test
    @DisplayName("Load UserDetails instance by username")
    void loadUserByUsername() {

        when(userService.findByUsername(user.getUsername())).thenReturn(user);

        assertThat(springUserDetailsService.loadUserByUsername(user.getUsername()))
                .isNotNull()
                .isEqualTo(userDetails);

        verify(userService, Mockito.times(1)).findByUsername(Mockito.anyString());

    }

}
