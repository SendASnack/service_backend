package com.example.service_backend.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.example.service_backend.model.Product;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ProductRepositoryTest {

    @Autowired
    private ProductsRepository productRepository;

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
    void setUp() {
        product = new Product();
        product.setName("Product1");
        product.setDescription("My Product1");
        product.setCategory("HAMBURGER");
        product.setPrice(5.00);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Find product by name")
    void findByName() {

        productRepository.save(product);

        Product loadedProduct = productRepository.findByName(product.getName());

        assertThat(loadedProduct)
                .isNotNull()
                .isEqualTo(product);

    }

    @Test
    @DisplayName("Find non-existent product by name")
    void findNonExistentProductByName() {
        Product loadedProduct = productRepository.findByName("ProductNotInDB");
        assertThat(loadedProduct).isNull();
    }

    @Test
    @DisplayName("Check if product exists by name")
    void existsByName() {

        productRepository.save(product);
        assertThat(productRepository.existsByName(product.getName())).isTrue();

    }

    @Test
    @DisplayName("Check non-existent product by name")
    void noExistsByName() {
        assertThat(productRepository.existsByName("ProductNotInDB")).isFalse();
    }

}