package com.linh.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    String fullname;
    String phone;
    String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    public enum Role {
        PURCHASER, WAREHOUSE, ACCOUNTANT, MANAGER
    }
}