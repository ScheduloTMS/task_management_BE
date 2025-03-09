package com.taskmanagement.task.Security; 

import com.taskmanagement.task.Entity.Users;
import com.taskmanagement.task.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Users users = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found with userId: " + userId));

        return User.builder()
                .username(users.getUserId())
                .password(users.getPassword())
                .roles(users.getRole())
                .build();
    }
}