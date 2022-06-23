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
public class ProductsControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductsService productService;

    private HttpHeaders httpHeaders; 

    private HttpHeaders httpHeadersAdmin;

    private Product product;

    private UserDAO admin;
    
    private UserDAO user;

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
        user = new UserDAO("Hugo1307", "hugogoncalves13@ua.pt", "12345", "hugo", "919312945", address);
        
        restTemplate.postForEntity("/api/auth/register", user, MessageResponse.class);
        ResponseEntity<AuthTokenResponse> response = restTemplate.postForEntity("/api/auth/login", new LoginRequest(user.getEmail(), user.getPassword()), AuthTokenResponse.class);

        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(response.getBody().getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        admin = new UserDAO("admin", "admin@ua.pt", "admin123", "admin", "919312945", address);
        
        restTemplate.postForEntity("/api/auth/register", admin , MessageResponse.class);

        ResponseEntity<AuthTokenResponse> response2 = restTemplate.postForEntity("/api/auth/login", new LoginRequest(admin.getEmail(), admin.getPassword()), AuthTokenResponse.class);

        httpHeadersAdmin = new HttpHeaders();
        httpHeadersAdmin.setBearerAuth(response2.getBody().getToken());
        httpHeadersAdmin.setContentType(MediaType.APPLICATION_JSON);

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
       productService.removeAll();
    }

    @Test
    @DisplayName("Create product")
    void createProduct(){

        assertThat(productService.getAllProducts()).isNotNull().isEmpty();

        ResponseEntity<MessageResponse> response = restTemplate.postForEntity("/api/admin/product", new HttpEntity<>(product, httpHeadersAdmin), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo("The product was successfully registered!");

        assertThat(productService.getAllProducts()).isNotNull().hasSize(1).doesNotContainNull();
    }

    @Test
    @DisplayName("Delete product")
    void deleteProduct() {

        Long saved = productService.save(product);
        product.setId(saved);

        assertThat(productService.getAllProducts()).isNotNull().isNotEmpty().doesNotContainNull().hasSize(1);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(String.format("/api/admin/product/%s", product.getId()), HttpMethod.DELETE, new HttpEntity<>(httpHeadersAdmin), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo("The product was successfully deleted!");

        assertThat(productService.getAllProducts()).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("Create product with wrong permissions")
    void createProductWithWrongPermissions() {

        assertThat(productService.getAllProducts()).isNotNull().isEmpty();

        ResponseEntity<MessageResponse> response = restTemplate.postForEntity("/api/admin/product", new HttpEntity<>(product, httpHeaders), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo("Current user does not have permission for this action.");

        assertThat(productService.getAllProducts()).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("Delete non existing product")
    void deleteNonExistingProduct() {

        Long randID = (long) 1234;

        assertThat(productService.getAllProducts()).isNotNull().isEmpty();

        ResponseEntity<MessageResponse> response = restTemplate.exchange( String.format("/api/admin/product/%s", randID), HttpMethod.DELETE, new HttpEntity<>(httpHeadersAdmin), MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).extracting(MessageResponse::getMessage).isEqualTo(String.format("The product %s could not be found.", randID));

    }

}