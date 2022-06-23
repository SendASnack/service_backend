package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.service_backend.exception.implementations.AlreadyExistentUserException;
import com.example.service_backend.exception.implementations.UserNotFoundException;
import com.example.service_backend.model.Costumer;
import com.example.service_backend.repository.CostumerRepository;

@Service
public class CostumerService {

    private final CostumerRepository userRepository;

    @Autowired
    public CostumerService(CostumerRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    public Costumer findByUsername(String username) {

        if (!userRepository.existsByUsername(username))
            throw new UserNotFoundException(String.format("The user %s could not be found.", username));
        return userRepository.findByUsername(username);

    }

    @NonNull
    public Costumer findByEmail(String email) {

        if (!userRepository.existsByEmail(email))
            throw new UserNotFoundException(String.format("The user %s could not be found.", email));
        return userRepository.findByEmail(email);

    }

    public void registerUser(Costumer costumer) {

        if (userRepository.existsByUsername(costumer.getUsername()) || userRepository.existsByEmail(costumer.getEmail()))
            throw new AlreadyExistentUserException("The provided username is already taken.");
        userRepository.save(costumer);

    }

    public void updateUser(Costumer costumer){
        userRepository.save(costumer);
    }

    public List<Costumer> getAllUsers() {
        return userRepository.findAll();
    }

    public void removeAll() {
        userRepository.deleteAll();
    }

}