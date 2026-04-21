package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				// ✅ CSRF MUST be disabled for APIs
				.csrf(csrf -> csrf.disable())

				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

						// ✅ ALWAYS allow actuator
						.requestMatchers("/actuator/**").permitAll().requestMatchers("/api/participants/**").permitAll()

						// ✅ PUBLIC AUTH ENDPOINTS (INCLUDING REGISTER)
						.requestMatchers("/api/auth/login", "/api/auth/register", "/api/admin/auth/login").permitAll()

						.requestMatchers("/api/incentives/**").permitAll().requestMatchers("/api/incentives")
						.permitAll()

						.requestMatchers("/api/programs/**").permitAll()

						.requestMatchers("/api/auth/login", "/api/auth/register", "/api/admin/**").permitAll()

						.requestMatchers("/api/programs/**").permitAll()

						.requestMatchers("/api/applications/**").permitAll().requestMatchers("/api/applications")
						.permitAll()

						.requestMatchers("/api/projects/**").permitAll().requestMatchers("/api/projects").permitAll()

						// ✅ everything else requires JWT
						.anyRequest().authenticated());

		return http.build();
	}
}