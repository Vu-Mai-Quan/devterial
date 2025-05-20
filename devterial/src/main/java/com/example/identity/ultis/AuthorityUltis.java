package com.example.identity.ultis;

import com.example.identity.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;



@Component
public class AuthorityUltis {

	public String getCurrentNameUser() {
		Authentication auth = getAuthentication();
        return auth.getName();
	}

	public Set<String> getAllRoleTagertUser(Object obj) {
		if (!(obj instanceof User)) {
            return getAuthentication().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
		}
        return ((User) obj).getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

	}

	private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
	}

	public boolean isCurrentNameLogin(String name) {
		return name.equals(getCurrentNameUser());
	}


}
