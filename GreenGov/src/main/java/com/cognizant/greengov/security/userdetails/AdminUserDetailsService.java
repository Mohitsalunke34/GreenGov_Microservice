package com.cognizant.greengov.security.userdetails;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cognizant.greengov.model.register_login.Admin;
import com.cognizant.greengov.repository.AdminRepository;

@Service
public class AdminUserDetailsService implements UserDetailsService {

	private final AdminRepository adminRepo;

	public AdminUserDetailsService(AdminRepository adminRepo) {
		this.adminRepo = adminRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

		return new org.springframework.security.core.userdetails.User(admin.getUsername(), admin.getPasswordHash(),
				admin.isActive(), true, true, true, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}
}