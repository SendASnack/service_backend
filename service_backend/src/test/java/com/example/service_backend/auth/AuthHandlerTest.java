package com.example.service_backend.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.service_backend.security.auth.AuthHandler;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private AuthHandler authHandler;

    @Test
    void getCurrentUsername() {
        when(authHandler.getCurrentUsername()).thenReturn("Hugo1307");
        assertThat(authHandler.getCurrentUsername()).isEqualTo("Hugo1307");
    }

}