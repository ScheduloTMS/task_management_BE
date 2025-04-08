package com.taskmanagement.task.Security;

import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Attempting login with email: " + email); // Debug log

        Users users = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> {
                    System.out.println("User not found in DB or is soft deleted: " + email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        System.out.println("User found: " + users.getUserId() + " (" + users.getRole() + ")");

        return new User(
                users.getEmail(),
                users.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole()))
        );
    }
}

