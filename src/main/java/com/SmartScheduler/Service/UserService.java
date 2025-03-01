package com.SmartScheduler.Service;

import org.springframework.stereotype.Service;

import com.SmartScheduler.Entity.User;
import com.SmartScheduler.Repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            System.out.println("Failed to find user with the provided Email: " + email);
            return null; 
        }
    }
}
