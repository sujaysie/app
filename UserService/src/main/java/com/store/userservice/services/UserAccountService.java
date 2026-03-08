package com.store.userservice.services;

import com.store.userservice.dtos.SignupRequest;
import com.store.userservice.models.User;
import com.store.userservice.models.AuthProvider;
import com.store.userservice.models.Role;
import com.store.userservice.repo.RoleRepository;
import com.store.userservice.repo.UserRepository;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserAccountService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserRepository userRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }
    @Transactional
    public User changeRole(Long userId,String role) {
        var user = userRepository.findById(userId).orElseThrow();
        if (user.getRoles().stream().anyMatch(r -> r.getName().equals(role))) {
            log.warn("User already is {}, no change made", role);
            return null;
        }
        var userFromDb = userRepository.findById(user.getId()).get();
        var roles = userFromDb.getRoles();
        roles.add(new Role(role));
        userFromDb.setRoles(roles);
        userRepository.save(userFromDb);
        return userFromDb;
    }
}
