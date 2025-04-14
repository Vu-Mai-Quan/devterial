package com.example.identity.ultis;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.identity.model.User;

@Component
public class AuthorityUltis {

	public String getCurrentNameUser() {
		Authentication auth = getAuthentication();
		String currentUsername = auth.getName();
		return currentUsername;
	}

	public Set<String> getAllRoleTagertUser(Object obj) {
		if (!(obj instanceof User)) {
			Set<String> currentRoles = getAuthentication().getAuthorities().stream()
					.map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
			return currentRoles;
		} else if (obj instanceof User) {
			Set<String> currentRoles = ((User) obj).getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toSet());
			return currentRoles;
		}
		return null;
	}

	private Authentication getAuthentication() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		return auth;
	}

	public boolean isCurrentNameLogin(String name) {
		return name.equals(getCurrentNameUser());
	}
	
	
}
