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
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Users users = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found with userId: " + userId));

        return new User(
                users.getUserId(),
                users.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole()))
        );

    }
}