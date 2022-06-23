package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.service_backend.exception.implementations.UserNotFoundException;
import com.example.service_backend.repository.CartInfoRepository;
import com.example.service_backend.model.CartInfo;
import com.example.service_backend.model.Product;

@Service
public class CartInfoService {

    private final CartInfoRepository cartInfoRepository;

    @Autowired
    public CartInfoService(CartInfoRepository cartInfoRepository) {
        this.cartInfoRepository = cartInfoRepository;
    }

    @NonNull
    public Optional<CartInfo> findById(Long id) {

        //if (!cartInfoRepository.existsById(id))
            //throw new UserNotFoundException(String.format("The cart %s could not be found.", id));
        return cartInfoRepository.findById(id);

    }

    @NonNull
    public Optional<CartInfo> findByProduct(Product product) {

        if (!cartInfoRepository.existsByProduct(product))
            throw new UserNotFoundException("CartInfo not found!");
        return cartInfoRepository.findByProduct(product);

    }

    public void registerCart(CartInfo cart) {

        if (cartInfoRepository.existsById(cart.getId()))
            throw new UserNotFoundException("The provided username is already taken.");
        cartInfoRepository.save(cart);

    }

    public void updateCart(CartInfo cart){
        cartInfoRepository.save(cart);
    }

    public List<CartInfo> getAllCarts() {
        return cartInfoRepository.findAll();
    }

    public Long save(CartInfo cart) {
        CartInfo savedEntity = cartInfoRepository.save(cart);
        return savedEntity != null ? savedEntity.getId() : null;
    }

    public void deleteCart(CartInfo cartInfo){
        cartInfoRepository.delete(cartInfo);
    }

    public void removeAll() {
        cartInfoRepository.deleteAll();
    }

}