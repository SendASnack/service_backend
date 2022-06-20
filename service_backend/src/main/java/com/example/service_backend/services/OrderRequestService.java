package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.service_backend.exception.implementations.OrderNotFoundException;
import com.example.service_backend.model.OrderRequest;
import com.example.service_backend.model.Costumer;
import com.example.service_backend.repository.OrderRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderRequestService {

    private final OrderRequestRepository orderRequestRepository;

    @Autowired
    public OrderRequestService(OrderRequestRepository orderRequestRepository) {
        this.orderRequestRepository = orderRequestRepository;
    }

    public Optional<OrderRequest> getById(Long orderId) {
        return orderRequestRepository.findById(orderId);
    }

    public List<OrderRequest> getAllOrders() {
        return orderRequestRepository.findAll();
    }

    public List<OrderRequest> getAllOrdersFromCostumer(Costumer costumer) {
        return orderRequestRepository.findAllByCostumerId(costumer);
    }

    public Long save(OrderRequest order) {
        OrderRequest savedEntity = orderRequestRepository.save(order);
        return savedEntity != null ? savedEntity.getId() : null;
    }

    public void delete(OrderRequest order) {
        if (!orderRequestRepository.existsById(order.getId()))
            throw new OrderNotFoundException("Order Not Found");
        orderRequestRepository.delete(order);
    }

}