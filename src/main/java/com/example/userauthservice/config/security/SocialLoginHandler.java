package com.example.userauthservice.config.security;

import com.example.userauthservice.models.User;
import com.example.userauthservice.services.JwtService;
import com.example.userauthservice.services.SocialLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SocialLoginHandler implements AuthenticationSuccessHandler {

    private final SocialLoginService socialLoginService;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken login = (OAuth2AuthenticationToken) authentication;

        String provider = login.getAuthorizedClientRegistrationId(); // google

        /// "Github" -> Github's id; "Google" -> Google's sub
        String providerUserId = login.getName(); // 121312312

        User user = socialLoginService.findOrCreate(provider, providerUserId);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + jwtService.generateToken(user) + "\"}");
    }
}
