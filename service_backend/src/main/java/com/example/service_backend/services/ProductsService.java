package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.service_backend.exception.implementations.AlreadyExistentUserException;
import com.example.service_backend.exception.implementations.UserNotFoundException;
import com.example.service_backend.repository.ProductsRepository;
import com.example.service_backend.model.Product;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @NonNull
    public Product findByName(String name) {

        if (!productsRepository.existsByName(name))
            throw new UserNotFoundException(String.format("The product %s could not be found.", name));
        return productsRepository.findByName(name);

    }

    @NonNull
    public Optional<Product> findById(Long id) {

        if (!productsRepository.existsById(id))
            throw new UserNotFoundException(String.format("The product %s could not be found.", id));
        return productsRepository.findById(id);

    }

    public void registerProduct(Product product) {

        if (productsRepository.existsByName(product.getName()))
            throw new AlreadyExistentUserException("The provided product already exists.");
        productsRepository.save(product);

    }

    public void updateProduct(Product product){
        productsRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    public void deleteProduct(Product product){
        productsRepository.delete(product);
    }

    public void removeAll() {
        productsRepository.deleteAll();
    }

}