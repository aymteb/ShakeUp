package com.shakeup.backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shakeup.backend.repository.UserAccountRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

	private final UserAccountRepository users;

	public DbUserDetailsService(UserAccountRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return users.findByEmailIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}
}
