package com.cognizant.greengov.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cognizant.greengov.security.userdetails.AdminUserDetailsService;
import com.cognizant.greengov.security.userdetails.UserAccountUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserAccountUserDetailsService userDetailsService;
	private final AdminUserDetailsService adminDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserAccountUserDetailsService userDetailsService,
			AdminUserDetailsService adminDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.adminDetailsService = adminDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		String auth = req.getHeader("Authorization");
		if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
			chain.doFilter(req, res);
			return;
		}

		String token = auth.substring(7);
		if (!jwtService.isTokenValid(token)) {
			chain.doFilter(req, res);
			return;
		}

		String username = jwtService.extractUsername(token);
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails details = isAdminPath(req.getRequestURI()) ? adminDetailsService.loadUserByUsername(username)
					: userDetailsService.loadUserByUsername(username);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(details, null,
					details.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		chain.doFilter(req, res);
	}

	private static boolean isAdminPath(String path) {
		return path != null && path.startsWith("/api/admin");
	}
}