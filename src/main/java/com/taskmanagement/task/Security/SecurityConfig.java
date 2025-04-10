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

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/users").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET,"/api/users").authenticated()
                        .requestMatchers("/api/users/delete/**").hasRole("MENTOR")
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET, "/api/tasks/{task_id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tasks/profile").authenticated()
                        .requestMatchers("/api/assignments/**").authenticated()
                        .requestMatchers("/api/assignments/{taskId}/assign").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.POST, "/api/assignments").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/assignments/{taskId}/assign").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.PUT,"/api/assignments").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET,"/api/assignments/{taskId}").authenticated()
                        .requestMatchers("/api/assignments/{taskId}").authenticated()
                        .requestMatchers("/api/remarks/tasks/{taskId}").authenticated()
                        .requestMatchers("/api/remarks/{remarkId}").authenticated()
                        .requestMatchers("/api/notes/**").authenticated()
                        .requestMatchers("/ws/**","/ws","/ws/info").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/messages/history").authenticated()
                        .requestMatchers("/app").authenticated()
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