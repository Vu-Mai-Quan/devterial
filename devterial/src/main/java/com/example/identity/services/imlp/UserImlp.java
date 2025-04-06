package com.example.identity.services.imlp;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.mapper.UserMapper;
import com.example.identity.model.User;
import com.example.identity.repositories.JpaRepositoriyUser;
import com.example.identity.services.UserService;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserImlp implements UserService {

	JpaRepositoriyUser repo;
	UserMapper uMap;
	@Override
	@Transactional
	public User save(UserRequest data) {
		User user = uMap.userRequestToUser(data);	
		repo.save(user);
		return user;
	}

	@Override
	public List<UserRequest> getAll() {
		List<UserRequest> ls = repo.findAll().stream()
				.map((user) -> uMap.userToUserRequest(user))
				.toList();
		return ls;
	}

	@Override
	public UserRequest getOne(UUID id) {
		User user = repo.findById(id).orElseThrow(() -> new NoResultException("Không có user với id: " + id));
		return UserRequest.builder().id(id).fistName(user.getFistName()).lastName(user.getLastName())
				.username(user.getUsername()).dob(user.getDob()).build();
	}

	@Override
	public User update(UUID id,UserRequest rq) {
		if(!repo.existsById(id)) {
			throw new EntityExistsException("Không tồn tại user với id: "+ id);
		}
		User u = uMap.userRequestToUser(rq);
		repo.save(u);
		return u;
	}

}
