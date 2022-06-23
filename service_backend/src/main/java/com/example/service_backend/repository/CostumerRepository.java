package com.example.service_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.service_backend.model.Costumer;

@Repository
public interface CostumerRepository extends JpaRepository<Costumer, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Costumer findByUsername(String username);

    Costumer findByEmail(String email);

    void deleteAll();

}