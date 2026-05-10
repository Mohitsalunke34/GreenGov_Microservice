//Cryptographically validates token & extracts data
package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;

	public JwtService(@Value("${greengov.security.jwt.secret}") String secret) {
		this.key = buildHmacKey(secret);
	}

	// validate signature + expiration
	public boolean isTokenValid(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	// extract username (subject)
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// extract any claim (roles / authorities)
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return resolver.apply(claims);
	}

	// ===== helper =====
	private static SecretKey buildHmacKey(String secret) {
		try {
			byte[] decoded = Decoders.BASE64.decode(secret);
			return Keys.hmacShaKeyFor(decoded);
		} catch (Exception e) {
			return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}
	}
}