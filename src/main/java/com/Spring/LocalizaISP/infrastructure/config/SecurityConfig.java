package com.Spring.LocalizaISP.infrastructure.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {


    private final JWTFilter jwtFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth ->

                        auth.requestMatchers(HttpMethod.POST, "/usuarios/login", "/usuarios/criarConta").permitAll()
                                .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/usuarios/**").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers(HttpMethod.PUT, "/usuarios/**").hasAnyRole("ADMIN", "USUARIO")
                                .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAnyRole("ADMIN", "USUARIO")
                                .anyRequest().authenticated()


                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build();


    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

}
