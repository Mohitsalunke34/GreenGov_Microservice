//Reads & verifies JWT
package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		if (!jwtService.isTokenValid(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (SecurityContextHolder.getContext().getAuthentication() == null) {

			String username = jwtService.extractUsername(token);

			List<String> roles = jwtService.extractClaim(token, c -> c.get("roles", List.class));
			List<String> authorities = jwtService.extractClaim(token, c -> c.get("authorities", List.class));

			List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

			if (roles != null) {
				roles.forEach(r -> grantedAuthorities.add(new SimpleGrantedAuthority(r)));
			}

			if (authorities != null) {
				authorities.forEach(a -> grantedAuthorities.add(new SimpleGrantedAuthority(a)));
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
					grantedAuthorities);

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}
}