package com.example.service_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.service_backend.exception.implementations.AlreadyExistentUserException;
import com.example.service_backend.exception.implementations.UserNotFoundException;
import com.example.service_backend.repository.UserRepository;
import com.example.service_backend.model.User;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    public User findByUsername(String username) {

        if (!userRepository.existsByUsername(username))
            throw new UserNotFoundException(String.format("The user %s could not be found.", username));
        return userRepository.findByUsername(username);

    }

    @NonNull
    public User findByEmail(String email) {

        if (!userRepository.existsByEmail(email))
            throw new UserNotFoundException(String.format("The user %s could not be found.", email));
        return userRepository.findByEmail(email);

    }

    public void registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername()))
            throw new AlreadyExistentUserException("The provided username is already taken.");
        userRepository.save(user);

    }

    public void updateUserAdress(User user){
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void removeAll() {
        userRepository.deleteAll();
    }

}