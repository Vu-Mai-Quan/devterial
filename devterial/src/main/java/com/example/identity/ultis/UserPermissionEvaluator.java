package com.example.identity.ultis;

import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userPermissionEvaluator")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor()
public class UserPermissionEvaluator {
    AuthorityUltis authorityUltis;
    JpaRepositoriyUser jpaRepositoriyUser;

    public boolean canUpdateClient(UUID targetUserId) {
        User targetUser = jpaRepositoriyUser.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return targetUser.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_CLIENT"));
    }

    public boolean isSelf(UUID targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName().equals(jpaRepositoriyUser.findById(targetUserId)
                .map(User::getUsername)
                .orElse(null));
    }



}
