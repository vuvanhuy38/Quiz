package com.webquiz.config;

import com.webquiz.exception.CustomAuthFailureHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final CustomAuthFailureHandler customAuthFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                     "/swagger-resources/**", "/webjars/**").permitAll()
                    .requestMatchers("/login", "/api/auth/register", "/register", "/do-login", "/",
                                     "/logout").permitAll()
                    .requestMatchers("/api/exams/home", "/js/**", "/css/**", "/home",
                                     "/api/exams/search", "/api/exams/detail/*", "/detail/*").permitAll()
                    .requestMatchers("/api/attempts/**", "/user/history/**","/user/**").hasRole("USER")
                    .requestMatchers("/api/category/**", "/api/question-bank/**", "/api/exams/**",
                                     "/api/exam-question/**","/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/do-login")
                    .failureHandler(customAuthFailureHandler)
                    .defaultSuccessUrl("/handle-login-success", true)
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456"));
        System.out.println(encoder.encode("user"));
    }
}
