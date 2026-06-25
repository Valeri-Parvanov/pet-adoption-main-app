package com.petadoption.mainapp.service;

import com.petadoption.mainapp.entity.RoleName;
import com.petadoption.mainapp.entity.User;
import com.petadoption.mainapp.exception.EmailAlreadyExistsException;
import com.petadoption.mainapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String rawPassword, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(Set.of(roleService.getOrCreate(RoleName.USER)));

        User savedUser = userRepository.save(user);
        log.info("Registered new user: {}", savedUser.getEmail());
        return savedUser;
    }
}
