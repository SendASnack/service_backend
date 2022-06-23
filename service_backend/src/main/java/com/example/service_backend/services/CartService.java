package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.service_backend.exception.implementations.UserNotFoundException;
import com.example.service_backend.repository.CartRepository;
import com.example.service_backend.model.Cart;
import com.example.service_backend.model.Costumer;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @NonNull
    public Optional<Cart> findById(Long id) {

        //if (!cartRepository.existsById(id))
            //throw new UserNotFoundException(String.format("The cart %s could not be found.", id));
        return cartRepository.findById(id);

    }

    @NonNull
    public Optional<Cart> findByCostume(Costumer costumer) {

        if (!cartRepository.existsByCostumer(costumer))
            throw new UserNotFoundException("Cart not found!");
        return cartRepository.findByCostumer(costumer);

    }

    public void registerCart(Cart cart) {

        if (cartRepository.existsById(cart.getId()))
            throw new UserNotFoundException("The provided username is already taken.");
        cartRepository.save(cart);

    }

    public void updateCart(Cart cart){
        cartRepository.save(cart);
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public Long save(Cart cart) {
        Cart savedEntity = cartRepository.save(cart);
        return savedEntity != null ? savedEntity.getId() : null;
    }

    public void deleteCart(Cart cart){
        cartRepository.delete(cart);
    }

    public void removeAll() {
        cartRepository.deleteAll();
    }

}