package com.example.service_backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.example.service_backend.dao.UserDAO;
import com.example.service_backend.model.Address;
import com.example.service_backend.model.Product;
import com.example.service_backend.security.auth.AuthTokenResponse;
import com.example.service_backend.services.ProductsService;
import com.example.service_backend.utils.LoginRequest;
import com.example.service_backend.utils.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductsService productService;

    private HttpHeaders httpHeaders;

    private Product product;

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
    public void setUp() throws JsonMappingException, JsonProcessingException {
        Address address = new Address("city", "street", "postalCode");
        UserDAO user = new UserDAO("Hugo1307", "hugogoncalves13@ua.pt", "12345", "hugo", "919312945", address);
        
        restTemplate.postForEntity("/api/auth/register", user, MessageResponse.class);
        ResponseEntity<AuthTokenResponse> response = restTemplate.postForEntity("/api/auth/login", new LoginRequest(user.getEmail(), user.getPassword()), AuthTokenResponse.class);

        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(response.getBody().getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        product = new ObjectMapper().readValue("{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Product 1\",\n" +
                "    \"description\": \"My Product 1\",\n" +
                "    \"ingredients\": [\n" +
                "        \"Lettuce\",\n" +
                "        \"Tomato\"\n" +
                "        ]\n" +
                "    },\n" +
                "    \"category\": \"HAMBURGER\",\n" +
                "    \"price\": \"5.00\"\n" +
                "}", Product.class);

    }

    @AfterEach
    public void tearDown() {
       //productService.removeAll();
    }

    @Test
    @DisplayName("Add product to cart")
    void addProductToCart(){

        Long saved = productService.save(product);
        product.setId(saved);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(String.format("/api/cart/%s/add", product.getId()), HttpMethod.PATCH ,new HttpEntity<>(httpHeaders), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo("The product was successfully added to cart!");

    }

    @Test
    @DisplayName("Add non existing product to cart")
    void addNonExisgtingProductToCart(){

        Long radnId = (long) 1234;

        ResponseEntity<MessageResponse> response = restTemplate.exchange(String.format("/api/cart/%s/add", radnId), HttpMethod.PATCH ,new HttpEntity<>(httpHeaders), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo(String.format("The product %s could not be found.", radnId));

    }

    @Test
    @DisplayName("Remove product from cart")
    void removeProductFromCart(){

        Long saved = productService.save(product);
        product.setId(saved);

        restTemplate.exchange(String.format("/api/cart/%s/add", product.getId()), HttpMethod.PATCH ,new HttpEntity<>(httpHeaders), MessageResponse.class);
        ResponseEntity<MessageResponse> response2 = restTemplate.exchange(String.format("/api/cart/%s/remove", product.getId()), HttpMethod.PATCH ,new HttpEntity<>(httpHeaders), MessageResponse.class);

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).extracting(MessageResponse::getMessage).isEqualTo("The product was successfully removed from cart!");

    }

    @Test
    @DisplayName("Remove non existing product on cart")
    void removeNonExisgtingProductOnCart(){

        Long saved = productService.save(product);
        product.setId(saved);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(String.format("/api/cart/%s/remove", product.getId()), HttpMethod.PATCH ,new HttpEntity<>(httpHeaders), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo(String.format("The product %s could not be found on cart.", product.getId()));

    }


}