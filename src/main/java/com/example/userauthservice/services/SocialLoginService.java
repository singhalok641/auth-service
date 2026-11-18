package com.example.userauthservice.services;

import com.example.userauthservice.models.Role;
import com.example.userauthservice.models.User;
import com.example.userauthservice.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final UserRepo userRepo;

    public User findOrCreate(String provider, String provider_user_id) {
        return userRepo.findByProviderAndProviderUserId(provider, provider_user_id)
                .orElseGet(() -> {
                    User user = new User();
                    user.setProvider(provider);
                    user.setProviderUserId(provider_user_id);
                    user.setRole(Role.CUSTOMER);
                    return userRepo.save(user);
                });
    }
}
