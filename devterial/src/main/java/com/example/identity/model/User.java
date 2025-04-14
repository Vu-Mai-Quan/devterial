package com.example.identity.model;

import com.example.identity.enumvalue.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "user", indexes = { @Index(columnList = "username", name = "idx_username") })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity implements UserDetails {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;
	@Column(unique = true, length = 32)
	String username;
	@Column(length = 60)
	String password;
	@Column(length = 30)
	String fistName;
	@Column(length = 30)
	String lastName;

	LocalDate dob;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role", length = 13)
	Set<RoleEnum> role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getRole().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
				.collect(Collectors.toSet());
	}

}
