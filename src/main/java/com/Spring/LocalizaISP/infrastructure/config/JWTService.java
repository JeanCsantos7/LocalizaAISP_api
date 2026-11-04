package com.Spring.LocalizaISP.infrastructure.config;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JWTService {


    @Value("${jwt_secret}")
    private String SECRET;

    private Key getKeySecret() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateJWT(String email, String role) {


        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3100000))
                .signWith(getKeySecret())
                .compact();

    }

    public String validateToken(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getKeySecret())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();


    }




}
