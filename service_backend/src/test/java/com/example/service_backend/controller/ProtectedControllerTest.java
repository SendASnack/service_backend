package com.example.service_backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.example.service_backend.dao.UserDAO;
import com.example.service_backend.exception.ErrorDetails;
import com.example.service_backend.requests.ProfileRequest;
import com.example.service_backend.requests.LoginRequest;
import com.example.service_backend.requests.MessageResponse;
import com.example.service_backend.security.auth.AuthTokenResponse;
import com.example.service_backend.services.UserService;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
public class ProtectedControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    public static MariaDBContainer<?> mariaDb = new MariaDBContainer<>(DockerImageName.parse("mariadb"))
            .withDatabaseName("SendASnack_Service_Test")
            .withUsername("user1")
            .withPassword("password1")
            .withExposedPorts(3306)
            .waitingFor(new HttpWaitStrategy().forPort(3306)
                    .withStartupTimeout(Duration.ofMinutes(5)));

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",mariaDb::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDb::getUsername);
        registry.add("spring.datasource.password", mariaDb::getPassword);

    }

    @Autowired
    private UserService userService;

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO("Hugo1307", "hugogoncalves13@ua.pt", "12345", "Hugo", "919312945");
        userService.removeAll();
    }

    @AfterEach
    public void tearDown() {
        userService.removeAll();
    }

    @Test
    @DisplayName("Get profile with no user authenticathed")
    void informationFromNonAuthenticathedUser() {

        ResponseEntity<ErrorDetails> response = restTemplate.getForEntity("/api/profile", ErrorDetails.class);
        ErrorDetails errorDetailsResponse = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(errorDetailsResponse).isNotNull();
        assertThat(errorDetailsResponse).extracting(ErrorDetails::getTimestamp).isNotNull();
        assertThat(errorDetailsResponse).extracting(ErrorDetails::getMessage).isEqualTo("Full authentication is required to access this resource");

    }

    @Test
    @DisplayName("Get profile with user authenticathed")
    void informationFromAuthenticathedUser() {

        restTemplate.postForEntity("/api/auth/register", userDAO, MessageResponse.class);

        LoginRequest loginRequest = new LoginRequest(userDAO.getEmail(), userDAO.getPassword());

        ResponseEntity<AuthTokenResponse> response = restTemplate.postForEntity("/api/auth/login", loginRequest, AuthTokenResponse.class);

        ResponseEntity<ProfileRequest> response2 = restTemplate.getForEntity("/api/profile",ProfileRequest.class);
        ProfileRequest profileResponse = response2.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileResponse).isNotNull();

    }
    
}
