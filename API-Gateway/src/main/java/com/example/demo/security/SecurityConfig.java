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

					    // ✅ allow actuator
					    .requestMatchers("/actuator/**").permitAll()

					    // ✅ allow public APIs
					    .requestMatchers("/api/auth/**").permitAll()
					    .requestMatchers("/api/participants/**").permitAll()
					    .requestMatchers("/api/resources/**").permitAll()
					    .requestMatchers("/api/infrastructure/**").permitAll()
					    .requestMatchers("/api/notifications/**").permitAll()

					    .requestMatchers("/api/incentives/**").permitAll()
					    .requestMatchers("/api/disbursements/**").permitAll()
					    .requestMatchers("/api/programs/**").permitAll()
					    .requestMatchers("/api/projects/**").permitAll()
					    .requestMatchers("/api/applications/**").permitAll()
					    .requestMatchers("/api/compliance/**").permitAll()
					    .requestMatchers("/api/audits/**").permitAll()

<<<<<<< HEAD
						.requestMatchers("/api/auth/**", "/api/auth/register", "/api/admin/auth/login").permitAll()
=======
					    // ✅ ✅ ADD THIS LINE (THIS FIXES 403)
					    .requestMatchers("/api/reports/**").permitAll()
>>>>>>> 8e790dd833e824a582f916cead1ca4bd864d0f59

					    // 🔒 everything else secured
					    .anyRequest().authenticated()
					);

		return http.build();
	}
}