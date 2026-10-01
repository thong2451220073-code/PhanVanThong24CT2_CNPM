package com.cuahangcongnghe.baomat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class DichVuJwt {

    @Value("${jwt.secret}")
    private String chuoiBiMat;

    @Value("${jwt.expiration}")
    private long thoiGianHetHan;

    private SecretKey laySecretKey() {
        return Keys.hmacShaKeyFor(chuoiBiMat.getBytes(StandardCharsets.UTF_8));
    }

    public String taoToken(UserDetails userDetails) {
        return taoToken(new HashMap<>(), userDetails);
    }

    public String taoToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + thoiGianHetHan))
                .signWith(laySecretKey())
                .compact();
    }

    public String layEmailTuToken(String token) {
        return layMotClaim(token, Claims::getSubject);
    }

    public <T> T layMotClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = layTatCaClaim(token);
        return claimsResolver.apply(claims);
    }

    private Claims layTatCaClaim(String token) {
        return Jwts.parser()
                .verifyWith(laySecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean kiemTraTokenHopLe(String token, UserDetails userDetails) {
        final String email = layEmailTuToken(token);
        return email.equals(userDetails.getUsername()) && !kiemTraTokenHetHan(token);
    }

    private boolean kiemTraTokenHetHan(String token) {
        return layNgayHetHan(token).before(new Date());
    }

    private Date layNgayHetHan(String token) {
        return layMotClaim(token, Claims::getExpiration);
    }
}
