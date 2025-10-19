package com.washify.apis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity đại diện cho vai trò người dùng (ADMIN, STAFF, CUSTOMER, etc.)
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users") // Exclude collection để tránh lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Chỉ dùng field được đánh dấu @EqualsAndHashCode.Include
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Dùng ID để so sánh equality
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    @EqualsAndHashCode.Include // Dùng name để so sánh (business key)
    private String name; // Tên role (VD: ADMIN, STAFF, CUSTOMER)
    
    @Column(length = 255)
    private String description; // Mô tả vai trò
    
    // Many-to-Many với User (phía bị map)
    @JsonIgnore // Ngăn serialize collection này để tránh circular reference & LazyInitializationException
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
