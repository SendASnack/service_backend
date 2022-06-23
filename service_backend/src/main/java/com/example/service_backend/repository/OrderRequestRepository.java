package com.example.service_backend.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.service_backend.model.OrderRequest;
import com.example.service_backend.model.Costumer;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {

    @NonNull Optional<OrderRequest> findById(@NonNull Long id);

    List<OrderRequest> findAllByCostumerId(Costumer costumer);

}
