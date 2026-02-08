package com.store.userservice.services;

import com.store.userservice.models.User;
import com.store.userservice.models.AuthProvider;
import com.store.userservice.models.Role;
import com.store.userservice.repo.RoleRepository;
import com.store.userservice.repo.UserRepository;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GoogleOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.getOrDefault("name", email);

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        User user = userRepository.findByEmail(email)
                .orElseGet(User::new);
        user.setEmail(email);
        user.setName(name);
        user.setProvider(AuthProvider.GOOGLE);
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);

        return oauthUser;
    }
}
