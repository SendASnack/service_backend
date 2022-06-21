package com.example.service_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service_backend.exception.implementations.BadRequestException;
import com.example.service_backend.exception.implementations.ForbiddenOperationException;
import com.example.service_backend.exception.implementations.ProductNotFoundException;
import com.example.service_backend.model.Product;
import com.example.service_backend.requests.MessageResponse;
import com.example.service_backend.security.auth.AuthHandler;
import com.example.service_backend.services.ProductsService;

@RestController
@RequestMapping("/api")
public class ProductsController {

    private final AuthHandler authHandler;
    private final ProductsService productService;

    @Autowired
    public ProductsController(ProductsService productService, AuthHandler authHandler) {
        this.productService = productService;
        this.authHandler = authHandler;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(@RequestParam String category, @RequestParam String maxPrice) {

        List<Product> products = productService.getAllProducts();

        if (products.isEmpty())
            throw new BadRequestException("No avaiable products at the moment.");

        if (!(category.equals("")) || !(maxPrice.equals(""))){
            for (Product p : productService.getAllProducts()){
                if (!(p.getCategory().equals(category)) || p.getPrice()>Double.parseDouble(maxPrice))
                    products.remove(p);
            }
        }

        return products;
    }

    @PostMapping("/admin/product")
    public MessageResponse createProduct(@RequestBody Product product){

        if (!(authHandler.getCurrentUsername().equals("admin")))
            throw new ForbiddenOperationException("Current user does not have permission for this action.");

        productService.registerProduct(product);

        return new MessageResponse("The product was successfully registered!");
    } 

    @DeleteMapping("/admin/product/{productId}")
    public MessageResponse deleteProduct(@PathVariable Long productId){

        if (!(authHandler.getCurrentUsername().equals("admin")))
            throw new ForbiddenOperationException("Current user does not have permission for this action.");

        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();

        productService.deleteProduct(product);

        return new MessageResponse("The product was successfully deleted!");
    } 
    
}
