package com.app.advert.portal.controller;

import com.app.advert.portal.dto.UserListRequest;
import com.app.advert.portal.dto.UserRequestDto;
import com.app.advert.portal.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/management/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Api(value = "User management Controller", produces = MediaType.APPLICATION_JSON_VALUE, tags = {"User management"})
public class UserManagementController {

    private final UserService userService;

    @PostMapping("/addUser")
    @Operation(tags = {"User management"}, description = "Register new user")
    public ResponseEntity<?> registerNewUser(@RequestBody UserRequestDto userDto) {
        try {
            log.info("UserManagementController: Register new user");
            return userService.saveUser(userDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    @Operation(tags = {"User management"}, description = "Delete user")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
        try {
            log.info("UserManagementController: Delete user: " + userId);
            return userService.deleteUser(userId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @PutMapping("/update")
    @Operation(tags = {"User management"}, description = "Update user")
    @PreAuthorize("hasAnyAuthority('USER_WRITE', 'COMPANY_USER', 'COMPANY_ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UserRequestDto userDto) {
        try {
            log.info("UserManagementController: Update user: " + userDto.getId());
            return userService.updateUser(userDto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(tags = {"User management"}, description = "List of users")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> readUsersList(@RequestBody UserListRequest userListRequest) {
        try {
            log.info("UserManagementController: List of users");
            return userService.getUsers(userListRequest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/activate/{userId}")
    @Operation(tags = {"User management"}, description = "Activate user")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        try {
            log.info("UserManagementController: Activate user " + userId);
            return userService.activateUser(userId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
