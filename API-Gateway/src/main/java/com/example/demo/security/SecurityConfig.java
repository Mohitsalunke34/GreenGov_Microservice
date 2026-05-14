package com.example.demo.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            // ✅ ENABLE CORS (Spring Security 6 way)// to take request from react
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ✅ DISABLE CSRF (JWT based auth)
            .csrf(csrf -> csrf.disable())

            // ✅ STATELESS SESSION
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ AUTHORIZATION RULES
            .authorizeHttpRequests(auth -> auth

                // ✅ PUBLIC
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/auth/login").permitAll()

                // ✅ PROGRAMS
                .requestMatchers(HttpMethod.GET, "/api/programs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/programs/**")
                .hasAuthority("PROGRAM_MANAGER")

                // ✅ PROJECTS
                .requestMatchers(HttpMethod.POST, "/api/projects/**")
                .hasAnyRole("CITIZEN", "BUSINESS_OWNER")
                .requestMatchers(HttpMethod.PATCH, "/api/projects/**")
                .hasAuthority("PROGRAM_MANAGER")

                // ✅ INFRASTRUCTURE
                .requestMatchers(HttpMethod.POST, "/api/infrastructure/**")
                .hasAuthority("PROGRAM_MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/api/infrastructure/**")
                .hasAuthority("PROGRAM_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/infrastructure/**")
                .hasAuthority("PROGRAM_MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/infrastructure/**")
                .authenticated()

                // ✅ COMPLIANCE
                .requestMatchers(HttpMethod.POST, "/api/compliance/**")
                .hasAuthority("COMPLIANCE_OFFICER")

                // ✅ AUDIT
                .requestMatchers("/api/audits/**")
                .hasAuthority("AUDIT_MANAGER")

                // ✅ INCENTIVE
                .requestMatchers(HttpMethod.POST, "/api/incentives/**")
                .hasAuthority("DISBURSEMENT_OFFICER")

                // ✅ REPORTS
                .requestMatchers(HttpMethod.POST, "/api/reports/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()

                // ✅ EVERYTHING ELSE
                .anyRequest().authenticated()
            )

            // ✅ JWT FILTER
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ CORS CONFIGURATION SOURCE (USED BY SECURITY)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:3000")
        );

        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}