package com.cognizant.greengov.security.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cognizant.greengov.model.Enums.OfficerType;
import com.cognizant.greengov.model.Enums.PrimaryRole;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;

@Service
public class UserAccountUserDetailsService implements UserDetailsService {

	private final UserAccountRepository userRepo;

	public UserAccountUserDetailsService(UserAccountRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserAccount user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<GrantedAuthority> authorities = new ArrayList<>();

		// Base role
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getPrimaryRole().name()));

		// Officer-type based authorities
		if (user.getPrimaryRole() == PrimaryRole.OFFICER && user.getOfficerProfile() != null) {

			OfficerType type = user.getOfficerProfile().getOfficerType();

			switch (type) {
			case PROGRAM_MANAGER -> authorities.add(new SimpleGrantedAuthority("PROGRAM_MANAGER"));

			case DISBURSEMENT_OFFICER -> authorities.add(new SimpleGrantedAuthority("DISBURSEMENT_OFFICER"));

			case COMPLIANCE_OFFICER -> authorities.add(new SimpleGrantedAuthority("COMPLIANCE_OFFICER"));

			case AUDIT_MANAGER -> authorities.add(new SimpleGrantedAuthority("AUDIT_MANAGER"));
			}
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(),
				authorities);
	}

}