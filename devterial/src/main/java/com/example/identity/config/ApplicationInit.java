package com.example.identity.config;

import com.example.identity.enumvalue.RoleEnum;
import com.example.identity.model.Role;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.repositories.RoleRepositories;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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

    public ApplicationInit(@Value("${admin.name}") String aDMIN_NAME, @Value("${admin.password}") String pASSWORD,
                           JpaRepositoriyUser jpaRepositoriyUser, RoleRepositories roleRepositories) {
        super();
        ADMIN_NAME = aDMIN_NAME;
        PASSWORD = pASSWORD;
        this.jpaRepositoriyUser = jpaRepositoriyUser;
        this.roleRepositories = roleRepositories;
    }

    /**
     * Hàm chạy thêm role và user cho hệ thống khởi chạy lần đầy
     */
    @Bean
    ApplicationRunner applicationRunner(PasswordEncoder passwordEncoder) {
        return args -> {
            Set<String> roleNames = Arrays.stream(RoleEnum.values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

            Map<String, Role> existingRoles = roleRepositories.findAllById(roleNames)
                    .stream()
                    .collect(Collectors.toMap(Role::getName, Function.identity()));

            Set<Role> allRoles = roleNames.stream()
                    .map(roleName -> existingRoles.getOrDefault(roleName,
                            Role.builder()
                                    .name(roleName)
                                    .description("Đây là chức vụ : " + roleName)
                                    .build()))
                    .collect(Collectors.toSet());

            if (!allRoles.isEmpty()) {
                roleRepositories.saveAll(allRoles);
            }
            createAdminIfNotExists(allRoles, passwordEncoder);
        };
    }

    private void createAdminIfNotExists(Set<Role> setRole, PasswordEncoder encoder) {
        jpaRepositoriyUser.findByUsername(ADMIN_NAME)
                .ifPresentOrElse(
                        user -> {
                            if(user.getRole().isEmpty()){
                            user.setRole(setRole);
                            jpaRepositoriyUser.save(user);
                        }},
                        () -> {
                            User adminUser = User.builder()
                                    .username(ADMIN_NAME)
                                    .password(encoder.encode(PASSWORD))
                                    .role(setRole)
                                    .build();
                            jpaRepositoriyUser.save(adminUser);
                            log.info("Create admin success: {}", adminUser.getUsername());
                        }
                );
    }
}
