package com.example.service_backend.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service_backend.exception.implementations.ProductNotFoundException;
import com.example.service_backend.model.Order;
import com.example.service_backend.model.Product;
import com.example.service_backend.model.User;
import com.example.service_backend.requests.MessageResponse;
import com.example.service_backend.security.auth.AuthHandler;
import com.example.service_backend.services.ProductsService;
import com.example.service_backend.services.UserService;

@RestController
@RequestMapping("/api")
public class CartController {

    private final AuthHandler authHandler;
    private final UserService userService;
    private final ProductsService productsService;

    @Autowired
    public CartController(UserService userService, ProductsService productsService, AuthHandler authHandler) {
        this.userService = userService;
        this.productsService = productsService;
        this.authHandler = authHandler;
    }

    @PatchMapping("/cart/{productId}/add")
    public MessageResponse addProductToCart(@PathVariable Long productId) {

        Optional<Product> productOptional = productsService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();
        User res = userService.findByUsername(authHandler.getCurrentUsername());

        res.getCart().add(product);

        userService.updateUser(res);

        return new MessageResponse(Date.from(Instant.now()), "The product was successfully added to cart!");
    }

    @PatchMapping("/cart/{productId}/remove")
    public MessageResponse removeProductFromCart(@PathVariable Long productId) {

        Optional<Product> productOptional = productsService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();
        User res = userService.findByUsername(authHandler.getCurrentUsername());

        if (!res.getCart().contains(product))
            throw new ProductNotFoundException(String.format("The product %s could not be found on cart.", productId));

        res.getCart().remove(product);

        userService.updateUser(res);

        return new MessageResponse(Date.from(Instant.now()), "The product was successfully removed from cart!");
    }

    @PutMapping("cart/{productId}/clear")
    public MessageResponse clearProductFromCart(@PathVariable Long productId){

        Optional<Product> productOptional = productsService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();
        User res = userService.findByUsername(authHandler.getCurrentUsername());

        res.getCart().remove(product);

        userService.updateUser(res);

        return new MessageResponse(Date.from(Instant.now()), "The product was successfully removed from cart!");
    }

    @PostMapping("cart/order")
    public void createOrder(@RequestBody Order order){
        
    }
    
}
