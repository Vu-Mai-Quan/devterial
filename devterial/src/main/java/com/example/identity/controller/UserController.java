package com.example.identity.controller;

import com.example.identity.dto.request.UserRequest;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.dto.response.UserResponse;
import com.example.identity.enumvalue.StatusMessageEnum;
import com.example.identity.exeptionsglobal.ErrorModel;
import com.example.identity.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/user/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
    UserService service;

    /**
     * đăng kí user
     */
    @PostMapping("public/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest rq) {
        return ResponseEntity.ok(service.save(rq));
    }

    @GetMapping()
    public ResponseEntity<?> getAllUser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")int size, HttpServletRequest rq) {
        Page<UserResponse> lsUser = service.getAll(Pageable.ofSize(size).withPage(page));
        if(lsUser.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorModel(StatusMessageEnum.NOT_FOUND,"Không có bản ghi", rq.getRequestURI()));
        }
        return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, StatusMessageEnum.SUCCESS.getMessage(), lsUser));
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<GlobalResponse<UserResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity
                .ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, StatusMessageEnum.SUCCESS.getMessage(), service.getOne(id)));
    }


    @PutMapping("/update-user/{id}")
    public ResponseEntity<GlobalResponse<UserResponse>> putMethodName(@PathVariable UUID id,
                                                                      @RequestBody UserRequest entity) {
        try {
            UserResponse u = service.update(id, entity);
            return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, "cập nhật thành công", u));
        } catch (AuthenticationException e) {
            e.fillInStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GlobalResponse<>(StatusMessageEnum.BAD_REQUEST,
                    String.format("cập nhật thất bại: %s", e.getMessage()), null));
        }

    }

}
