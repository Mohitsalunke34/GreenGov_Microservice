package com.cognizant.greengov.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).sessionManagement(s -> s.sessionCreationPolicy(
				SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth -> auth
						// 1. Public Authentication & Admin Login
						.requestMatchers("/api/admin/auth/login", "/api/auth/register", "/api/auth/login",
								"/api/notifications/trigger")
						.permitAll()

						// 2. Notification Endpoints
						.requestMatchers("/api/notifications/user/**").authenticated()
						.requestMatchers(HttpMethod.PATCH, "/api/notifications/*/read").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/notifications/**").hasRole("ADMIN")

						// 3. Infrastructure Endpoints (Newly Added)
						// Add/Update/Delete/Status: Restricted to Program Managers
						.requestMatchers(HttpMethod.POST, "/api/infrastructure/add").hasAuthority("PROGRAM_MANAGER")
						.requestMatchers(HttpMethod.PUT, "/api/infrastructure/update/**")
						.hasAuthority("PROGRAM_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/infrastructure/update-status")
						.hasAuthority("PROGRAM_MANAGER")
						.requestMatchers(HttpMethod.DELETE, "/api/infrastructure/delete/**")
						.hasAuthority("PROGRAM_MANAGER")
						// Viewing: Authenticated users can view details
						.requestMatchers(HttpMethod.GET, "/api/infrastructure/**").authenticated()

						// 4. Resources Endpoints
//                        .requestMatchers(HttpMethod.POST, "/api/resources/allocate").hasAuthority("ENVIRONMENTAL_OFFICER")
						.requestMatchers(HttpMethod.GET, "/api/resources").hasAuthority("PROGRAM_MANAGER")
						.requestMatchers("/api/resources/**")
						.hasAnyAuthority("ENVIRONMENTAL_OFFICER", "PROGRAM_MANAGER")

						// 5. Admin console
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// 6. Programs (GET public)
						.requestMatchers(HttpMethod.GET, "/api/programs").permitAll()

						// 7. Applications
						.requestMatchers(HttpMethod.POST, "/api/applications").permitAll()

						// 8. Incentives & Disbursements → Disbursement Officers only
						.requestMatchers(HttpMethod.POST, "/api/incentives/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/disbursements/**").hasAuthority("DISBURSEMENT_OFFICER")

						// 9. Programs can be created by citizen and business
						.requestMatchers(HttpMethod.POST, "/api/programs/**").hasRole("CITIZEN")
						.requestMatchers(HttpMethod.POST, "/api/programs/**").hasRole("BUSINESS_OWNER")

						// 10. Compliance → Compliance Officer
						.requestMatchers(HttpMethod.POST, "/api/compliance/**").hasAuthority("COMPLIANCE_OFFICER")

						// 11. Audits → Audit Manager
						.requestMatchers("/api/audits/**").hasAuthority("AUDIT_MANAGER")

						.requestMatchers("/api/reports/**").permitAll()

						// 12. Projects (GET public) and for patch and post it is only for PROGRAM Manager.
						.requestMatchers(HttpMethod.POST, "/api/projects/**").hasAuthority("PROGRAM_MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/projects/**").hasAuthority("PROGRAM_MANAGER")

//                        .requestMatchers(HttpMethod.POST, "/api/resources/**").permitAll()

						// 13. Catch-all
						.anyRequest().authenticated())

				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager() {
		return new ProviderManager(new AnonymousAuthenticationProvider("greengov-anon"));
	}
}