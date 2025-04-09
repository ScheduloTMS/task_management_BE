package com.taskmanagement.task.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // if using cookies/auth

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/users").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET,"/api/users").permitAll()
                        .requestMatchers("/api/users/delete/**").hasRole("MENTOR")
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/{task_id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tasks/profile").authenticated()
                        .requestMatchers("/api/assignments/**").authenticated()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/assignments/{taskId}/assign").hasRole("MENTOR")
                        .requestMatchers("/api/assignments").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT,"/api/assignments").hasRole("MENTOR")
                        .requestMatchers("/api/assignments/{taskId}").authenticated()
                        .requestMatchers("/api/remarks/tasks/{taskId}").authenticated()
                        .requestMatchers("/api/remarks/{remarkId}").authenticated()
                        .requestMatchers("/api/notes/**").authenticated()
                        .requestMatchers("/ws").permitAll()
                        .requestMatchers("/ws/**","/ws/info").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}