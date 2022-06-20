package com.example.service_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.service_backend.model.Cart;
import com.example.service_backend.model.CartInfo;
import com.example.service_backend.model.Product;

import lombok.NonNull;

@Repository
public interface CartInfoRepository extends JpaRepository<CartInfo, Long> {

    @NonNull
    Optional<CartInfo> findById(Long id);

    @NonNull
    Optional<CartInfo> findByProduct(Product product);

    List<CartInfo> findByCart(Cart cart, Sort sort);

    boolean existsById(Long id);

    boolean existsByProduct(Product product);

}