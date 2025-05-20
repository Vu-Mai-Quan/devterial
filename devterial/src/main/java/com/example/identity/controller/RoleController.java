package com.example.identity.controller;


import com.example.identity.dto.request.RoleRq;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.enumvalue.StatusMessageEnum;
import com.example.identity.services.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/auth/role")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class RoleController {

    RoleService roleService;

    @GetMapping("")
    public ResponseEntity<?> getALl(){
        return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, "Success", roleService.getAll()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id,@RequestBody RoleRq roleRq){
        return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, "Success", roleService.update(id,roleRq)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id){
        return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS, "Success", roleService.getOne(id)));
    }
}
