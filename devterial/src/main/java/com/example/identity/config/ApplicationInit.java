package com.example.identity.config;

import com.example.identity.enumvalue.PermissionEnum;
import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.model.Permission;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.repositories.PermissionRepository;
import com.example.identity.repositories.RoleRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class ApplicationInit {

    private final RoleRepositories roleRepositories;
    private final String ADMIN_NAME;
    private final String PASSWORD;
    private final JpaRepositoriyUser jpaRepositoriyUser;
    private final PermissionRepository permissionRepository;

    public ApplicationInit(@Value("${admin.name}") String aDMIN_NAME, @Value("${admin.password}") String pASSWORD,
                           JpaRepositoriyUser jpaRepositoriyUser, RoleRepositories roleRepositories, PermissionRepository permissionRepository) {
        ADMIN_NAME = aDMIN_NAME;
        PASSWORD = pASSWORD;
        this.jpaRepositoriyUser = jpaRepositoriyUser;
        this.roleRepositories = roleRepositories;
        this.permissionRepository = permissionRepository;
    }

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(PasswordEncoder passwordEncoder) {
        return args -> {
            var roles = createRole();
            var permissions = createPermissions();

            if (!roles.isEmpty()) {
                roles = new HashSet<>(roleRepositories.saveAll(roles));
                createAdminIfNotExists(roles, passwordEncoder);
            }

            if (!permissions.isEmpty()) {
                permissions = new HashSet<>(permissionRepository.saveAll(permissions));
                var admin = roleRepositories.findById(RoleEnum.ADMIN.name())
                        .orElseThrow(() -> new RuntimeException("Admin role not found"));
                admin.setPermissions(permissions);
                roleRepositories.save(admin);
            }
        };
    }

    private void createAdminIfNotExists(Set<Role> roles, PasswordEncoder encoder) {
        var adminUser = jpaRepositoriyUser.findByUsername(ADMIN_NAME)
                .orElseGet(() -> {
                    var newAdmin = User.builder()
                            .username(ADMIN_NAME)
                            .password(encoder.encode(PASSWORD))
                            .role(roles)
                            .build();
                    jpaRepositoriyUser.save(newAdmin);
                    log.info("Created admin user: {}", ADMIN_NAME);
                    return newAdmin;
                });

        if (!roles.isEmpty() && (adminUser.getRole().isEmpty() || adminUser.getRole().size() != roles.size())) {
            adminUser.setRole(roles);
            jpaRepositoriyUser.save(adminUser);
            log.info("Updated admin roles");
        }
    }

    private Set<Role> createRole() {
        Set<String> roleNames = Arrays.stream(RoleEnum.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Map<String, Role> existingRoles = roleRepositories.findAllById(roleNames).stream()
                .collect(Collectors.toMap(Role::getName, Function.identity()));

        return roleNames.stream()
                .filter(name -> !existingRoles.containsKey(name))
                .map(name -> Role.builder()
                        .name(name)
                        .descriptions(RoleEnum.valueOf(name).getMessage())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Permission> createPermissions() {
        Set<String> permissionNames = Arrays.stream(PermissionEnum.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Map<String, Permission> existingPermissions = permissionRepository.findAllById(permissionNames).stream()
                .collect(Collectors.toMap(Permission::getName, Function.identity()));

        return permissionNames.stream()
                .filter(name -> !existingPermissions.containsKey(name))
                .map(name -> Permission.builder()
                        .name(name)
                        .descriptions(PermissionEnum.valueOf(name).getDescriptions())
                        .build())
                .collect(Collectors.toSet());
    }
}