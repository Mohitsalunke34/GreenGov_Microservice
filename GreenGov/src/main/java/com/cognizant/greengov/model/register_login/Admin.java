package com.cognizant.greengov.model.register_login;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins",
       indexes = @Index(name = "uk_admin_uname", columnList = "username", unique = true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Admin console login (separate path/realm)
    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active = true;
}