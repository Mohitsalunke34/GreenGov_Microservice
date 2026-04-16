package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;
	private final long expirationMillis;

	public JwtService(@Value("${greengov.security.jwt.secret}") String secret,
			@Value("${greengov.security.jwt.expiration-minutes}") long expirationMinutes) {

		this.key = buildKey(secret);
		this.expirationMillis = expirationMinutes * 60_000;
	}

	public String generateToken(String username, Map<String, Object> claims) {

		Instant now = Instant.now();

		return Jwts.builder().claims(claims).subject(username).issuedAt(Date.from(now))
				.expiration(new Date(now.toEpochMilli() + expirationMillis)).signWith(key, Jwts.SIG.HS256).compact();
	}

	private SecretKey buildKey(String secret) {
		try {
			return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		} catch (Exception e) {
			return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}
	}
}
