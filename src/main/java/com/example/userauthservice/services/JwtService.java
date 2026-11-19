package com.example.userauthservice.services;

import com.example.userauthservice.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Service
public class JwtService {

    private final KeyPair keyPair;
    private final String keyId = UUID.randomUUID().toString();
    private final long expirySeconds;
    private final String audience;

    // A new key pair every time the service starts. Tokens from before a restart stop working.
    // A real system keeps its keys in a safe place and changes them on a schedule.
    public JwtService(@Value("${jwt.expiry-seconds}") long expirySeconds,
                      @Value("${jwt.audience}") String audience) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keyPair = generator.generateKeyPair();
        this.expirySeconds = expirySeconds;
        this.audience = audience;
    }

    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .header().keyId(keyId).and()
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .issuer("userauthservice")
                .audience().add(audience).and()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirySeconds * 1000))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // The public key in the standard JSON form (a JWK). Anyone may read it.
    public Map<String, Object> publicKeyAsJwk() {
        RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return Map.of(
                "kty", "RSA",
                "use", "sig",
                "alg", "RS256",
                "kid", keyId,
                "n", encoder.encodeToString(unsignedBytes(key.getModulus())),
                "e", encoder.encodeToString(unsignedBytes(key.getPublicExponent())));
    }

    private static byte[] unsignedBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        return bytes[0] == 0 ? Arrays.copyOfRange(bytes, 1, bytes.length) : bytes;
    }
}
