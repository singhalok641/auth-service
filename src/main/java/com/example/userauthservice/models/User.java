package com.example.userauthservice.models;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_user_id"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String passHash;

    // Null for users who user password to login; SQL allows (null, null) cols
    private String provider;
    private String providerUserId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
