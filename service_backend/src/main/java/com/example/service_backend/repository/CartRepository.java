package com.example.service_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.service_backend.model.Cart;
import com.example.service_backend.model.Costumer;

import lombok.NonNull;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @NonNull
    Optional<Cart> findById(Long id);

    @NonNull
    Optional<Cart> findByCostumer(Costumer costumer);

    boolean existsById(Long id);

    boolean existsByCostumer(Costumer costumer);

}