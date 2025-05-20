package com.example.identity.controller;

import com.example.identity.dto.request.PermissionRq;
import com.example.identity.dto.response.GlobalResponse;
import com.example.identity.enumvalue.StatusMessageEnum;
import com.example.identity.services.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/permisstion")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionController {
    PermissionService service;

    @PostMapping("")
    ResponseEntity<GlobalResponse<?>> addPermisstion(@RequestBody PermissionRq permissionRq){
        try {
            return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS,"Thêm permission thành công", service.save(permissionRq)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("")
    ResponseEntity<GlobalResponse<?>>  getAllPermisstion(){
        if(service.getAll().isEmpty()){
            return ResponseEntity.badRequest().body(new GlobalResponse<>(StatusMessageEnum.SUCCESS,"Không có permission",null));
        }else {
            return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS,"Thêm permission thành công", service.getAll()));
        }
    }
    @DeleteMapping("/{id}")
    ResponseEntity<GlobalResponse<?>> deletePermission(@RequestParam() String id){
        service.delete(id);
        return ResponseEntity.ok(new GlobalResponse<>(StatusMessageEnum.SUCCESS,String.format("Xóa thành công permission: %s",id),null));
    }
}
