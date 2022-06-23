package com.example.service_backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.example.service_backend.model.Address;
import com.example.service_backend.security.auth.AuthTokenResponse;
import com.example.service_backend.services.CostumerService;
import com.example.service_backend.utils.AddressRequest;
import com.example.service_backend.utils.LoginRequest;
import com.example.service_backend.utils.MessageResponse;
import com.example.service_backend.utils.PaymentRequest;
import com.example.service_backend.utils.ProfileRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
public class ProfileControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CostumerService userService;

    private UserDAO userDAO;

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

    @BeforeEach
    public void setUp() {
        Address address = new Address("city", "street", "postalCode");
        userDAO = new UserDAO("Hugo1307", "hugogoncalves13@ua.pt", "12345", "Hugo", "919312945", address);
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + response.getBody().getToken());

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ProfileRequest> response2 = restTemplate.exchange("/api/profile",HttpMethod.GET, entity,ProfileRequest.class);
        ProfileRequest profileResponse = response2.getBody();

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileResponse).isNotNull();

    }

    @Test
    @DisplayName("Update address with wrong body request")
    void updateAddressWithWrongBodyRequest() {

        restTemplate.postForEntity("/api/auth/register", userDAO, MessageResponse.class);

        LoginRequest loginRequest = new LoginRequest(userDAO.getEmail(), userDAO.getPassword());

        ResponseEntity<AuthTokenResponse> response = restTemplate.postForEntity("/api/auth/login", loginRequest, AuthTokenResponse.class);

        AddressRequest addressRequest = new AddressRequest(null, null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + response.getBody().getToken());

        HttpEntity<?> entity = new HttpEntity<>(addressRequest, headers);

        ResponseEntity<ErrorDetails> response2 = restTemplate.postForEntity("/api/profile/address", entity, ErrorDetails.class);
        ErrorDetails messageResponse = response2.getBody();

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse).extracting(ErrorDetails::getTimestamp).isNotNull();
        assertThat(messageResponse).extracting(ErrorDetails::getMessage).isEqualTo("Please provide a valid request body.");

    }

    @Test
    @DisplayName("Update payment")
    void updatePayment() {

        restTemplate.postForEntity("/api/auth/register", userDAO, MessageResponse.class);

        LoginRequest loginRequest = new LoginRequest(userDAO.getEmail(), userDAO.getPassword());

        ResponseEntity<AuthTokenResponse> response = restTemplate.postForEntity("/api/auth/login", loginRequest, AuthTokenResponse.class);

        PaymentRequest paymentRequest = new PaymentRequest("", "Visa", "", "456");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + response.getBody().getToken());

        HttpEntity<?> entity = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<MessageResponse> response2 = restTemplate.postForEntity("/api/profile/payment", entity, MessageResponse.class);
        MessageResponse errorDetailsResponse = response2.getBody();

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(errorDetailsResponse).isNotNull();
        assertThat(errorDetailsResponse).extracting(MessageResponse::getTimestamp).isNotNull();
        assertThat(errorDetailsResponse).extracting(MessageResponse::getMessage).isEqualTo("The user information was successfuly changed!");

    }
    
}
