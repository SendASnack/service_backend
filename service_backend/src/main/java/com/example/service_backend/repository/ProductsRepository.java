package com.example.service_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.service_backend.model.Product;

import lombok.NonNull;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {

    @NonNull
    Optional<Product> findById(Long id);

    boolean existsById(Long id);

    boolean existsByName(String name);

    Product findByName(String name);

    void deleteAll();

}