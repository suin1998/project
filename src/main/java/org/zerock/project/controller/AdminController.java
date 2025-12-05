package org.zerock.project.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.UserListDTO;
import org.zerock.project.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final UserService userService;

//    @GetMapping("/users")
//    public String adminUsersPage() {
//        return "admin/adminUserList";
//    }

    @GetMapping("/users")
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        log.info("API GET /admin/api/users 요청: 모든 사용자 목록 조회");

        // TODO: Security를 사용하여 현재 사용자가 ADMIN 권한인지 체크하는 로직 추가 필요

        List<UserListDTO> userList = userService.getAllUsers();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        log.info("API DELETE /admin/api/user/{} 요청: 사용자 삭제", userId);

        // TODO: Security를 사용하여 현재 사용자가 ADMIN 권한인지 체크하는 로직 추가 필요

        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content (삭제 성공)
        } catch (EntityNotFoundException e) {
            log.warn("사용자 삭제 실패: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            log.error("사용자 삭제 중 예외 발생: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}