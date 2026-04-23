package com.cognizant.greengov.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Minimal JWT helper for JJWT 0.12.x - token creation:
 * builder().claims().subject().issuedAt().expiration().signWith(key,
 * Jwts.SIG.HS256) - token parsing:
 * Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
 */
@Service
public class JwtService {

	private final SecretKey key;
	private final long expirationMillis;

	public JwtService(@Value("${greengov.security.jwt.secret}") String secret,
			@Value("${greengov.security.jwt.expiration-minutes}") long expirationMinutes) {
		this.key = buildHmacKey(secret);
		this.expirationMillis = expirationMinutes * 60_000;
	}

	// Create a signed JWT for the given subject with extra claims. 
	public String generateToken(String username, Map<String, Object> claims) {
		Instant now = Instant.now();
		return Jwts.builder().claims(claims) // 0.12.x (no setClaims)
				.subject(username) // 0.12.x (no setSubject)
				.issuedAt(Date.from(now)) // 0.12.x (no setIssuedAt)
				.expiration(new Date(now.toEpochMilli() + expirationMillis)) // 0.12.x
				.signWith(key, Jwts.SIG.HS256) // 0.12.x algorithm registry
				.compact();
	}

	// Extract the username (subject) from the token. 
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Validate signature/expiration; returns false if parsing fails. 
	public boolean isTokenValid(String token) {
		try {
			// 0.12.x parsing API
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token); // throws if invalid/expired
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	// Generic claim resolver for callers that need custom claim values. 
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); // (was getBody()
																										// in older
																										// versions)
		return resolver.apply(payload);
	}

	// ---------- helpers ----------

	private static SecretKey buildHmacKey(String secret) {
		byte[] raw;
		try {
			// Prefer Base64 if provided (recommended for key length)
			raw = Decoders.BASE64.decode(secret);
		} catch (Exception e) {
			// Fallback to literal bytes (ensure length >= 256-bit for HS256)
			raw = secret.getBytes(StandardCharsets.UTF_8);
		}
		return Keys.hmacShaKeyFor(raw);
	}
}