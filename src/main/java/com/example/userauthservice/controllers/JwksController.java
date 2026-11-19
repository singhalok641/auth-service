package com.example.userauthservice.controllers;

import com.example.userauthservice.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {
    private final JwtService jwtService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return Map.of("keys", List.of(jwtService.publicKeyAsJwk()));
    }
}
