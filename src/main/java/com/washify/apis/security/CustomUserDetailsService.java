package com.washify.apis.security;

import com.washify.apis.entity.User;
import com.washify.apis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService - Load user từ database
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmailOrPhone) throws UsernameNotFoundException {
        // Tìm user bằng username, email hoặc phone
        User user = userRepository.findByUsername(usernameOrEmailOrPhone)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmailOrPhone)
                        .orElseGet(() -> userRepository.findByPhone(usernameOrEmailOrPhone)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "User not found with username/email/phone: " + usernameOrEmailOrPhone))));

        // Check if user is deleted (soft delete check via deletedAt field)
        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("User has been deleted: " + usernameOrEmailOrPhone);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getDeletedAt() != null || !user.getIsActive())
                .build();
    }

    /**
     * Load user by ID
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("User has been deleted with id: " + id);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
