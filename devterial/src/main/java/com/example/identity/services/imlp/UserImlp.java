package com.example.identity.services.imlp;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.LoginResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.mapper.UserMapper;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.services.AuthService;
import com.example.identity.services.BaseService;
import com.example.identity.ultis.AuthorityUltis;
import com.example.identity.ultis.JwtConfig;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserImlp implements BaseService<UserRequest, UserResponse>, AuthService {

	JpaRepositoriyUser repo;
	UserMapper uMap;
	PasswordEncoder endCode;
	AuthorityUltis authorityUltis;
	@NonFinal
	JwtConfig jwtConfig;
	@NonFinal
	Key key;
	Logger logger = org.slf4j.LoggerFactory.getLogger(UserImlp.class);

	@Autowired
	public void setKey(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
		this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(jwtConfig.getSecretKey().getBytes()));
	}

	@Override
	@Transactional
	public UserResponse save(UserRequest data) {
		User user = uMap.userRequestToUser(data);
		user.setPassword(endCode.encode(data.getPassword()));
		repo.save(user);
		return uMap.userToUserResponse(user);
	}

	@Override
	public List<UserResponse> getAll() {

		List<UserResponse> ls = repo.findAll().stream().map((user) -> uMap.userToUserResponse(user)).toList();
		return ls;
	}

	@Override
	@PostAuthorize("returnObject.username == authentication.name")
	public UserResponse getOne(UUID id) {
		User user = repo.findById(id).orElseThrow(() -> new NoResultException("Không có user với id: " + id));
		return uMap.userToUserResponse(user);
	}

	@Override
	@Transactional
	public UserResponse update(UUID id, UserRequest rq) throws AuthenticationException {
		User targetUser = repo.findById(id)
				.orElseThrow(() -> new EntityExistsException("Không tồn tại user với id: " + id));

		User updatedUser = uMap.userRequestToUser(rq);
		logger.info(updatedUser.getPassword());
		Set<String> currentRoles = authorityUltis.getAllRoleTagertUser(null);
		Set<String> targetRoles = authorityUltis.getAllRoleTagertUser(targetUser);

		boolean isSelf = authorityUltis.isCurrentNameLogin(targetUser.getUsername());
		boolean isAdmin = currentRoles.contains("ROLE_ADMIN");
		boolean isCustomer = currentRoles.contains("ROLE_CUSTOMER");
		boolean targetIsAdmin = targetRoles.contains("ROLE_ADMIN");
		boolean targetIsCustomer = targetRoles.contains("ROLE_CUSTOMER");
		boolean targetIsClient = targetRoles.contains("ROLE_CLIENT");

		// Phân quyền theo yêu cầu
		if (isAdmin) {
			// Admin được cập nhật tất cả, bao gồm username
			updatedUser.setId(id);
			updatedUser.setPassword(endCode.encode(rq.getPassword()));
			repo.save(updatedUser);
			return uMap.userToUserResponse(updatedUser);
		}

		if (isCustomer) {
			// CUSTOMER chỉ được cập nhật người có quyền CLIENT
			if (targetIsClient && !targetIsAdmin && !targetIsCustomer) {
				updatedUser.setId(id);
				updatedUser.setUsername(targetUser.getUsername());
				updatedUser.setPassword(endCode.encode(rq.getPassword()));// Không cho sửa username
				updatedUser.setRole(targetUser.getRole());
				repo.save(updatedUser);
				return uMap.userToUserResponse(updatedUser);
			}
		}

		if (isSelf && (currentRoles.contains("ROLE_CLIENT"))) {
			updatedUser.setId(id);
			updatedUser.setUsername(targetUser.getUsername());
			updatedUser.setPassword(endCode.encode(rq.getPassword()));// Không cho sửa username
			updatedUser.setRole(targetUser.getRole());
			repo.save(updatedUser);
			return uMap.userToUserResponse(updatedUser);
		}

		throw new AuthenticationException("Không đủ quyền cập nhật");

	}

	@Override
	public LoginResponse login(LoginRequest rq) {
		Optional<User> user = repo.findByUsername(rq.getUsername());
		if (user.isPresent() && endCode.matches(rq.getPassword(), user.get().getPassword())) {
			return LoginResponse.builder()
					.token(createToken(user.get().getId(), user.get().getUsername(), user.get().getRole().toArray()))
					.build();
		} else {
			throw new NoResultException("Thông tin tài khoản hoặc mật khẩu không chính xác");
		}

	}

	private String createToken(UUID id, String username, Object... object) {
		Date date = new Date(System.currentTimeMillis() + jwtConfig.getIssuedAt());
        return Jwts.builder().setSubject(username).claim("role", object).claim("id", id)
                .signWith(key, SignatureAlgorithm.HS512).setIssuer("vumaiquan.com").setExpiration(date).compact();
	}

	@Override
	public boolean isAuthencate(String token) {

		return false;
	}

}
