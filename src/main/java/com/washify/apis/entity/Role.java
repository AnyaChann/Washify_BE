package com.washify.apis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho vai trò người dùng (ADMIN, STAFF, CUSTOMER, etc.)
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name; // Tên role (VD: ADMIN, STAFF, CUSTOMER)
    
    @Column(length = 255)
    private String description; // Mô tả vai trò
    
    // Many-to-Many với User (phía bị map)
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
