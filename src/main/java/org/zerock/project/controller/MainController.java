package org.zerock.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    @GetMapping("/")
    public String mainPage() {
        log.info("GET / 요청: 메인 페이지로 이동");
        return "main";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/join")
    public String joinPage() {
        log.info("GET /join 요청: 회원가입 페이지로 이동");
        return "user/join";
    }

    @GetMapping("/myPage")
    public String myPage() {
        return "user/userEdit";
    }

    @GetMapping("/community")
    public String community() {
        return "community/userCommunity";
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable String id) {
        return "community/post";
    }

    @GetMapping("/AICoordinator")
    public String aiCoordinator() {
        return "Ai/AICoordinator";
    }

    @GetMapping("/myCloset")
    public String myCloset() {
        return "user/myCloset";
    }

//    @GetMapping("/admin/users")
//    public String adminUsersPage() {
//        log.info("GET /admin/users 요청: 관리자 회원 목록 페이지로 이동");
//        return "admin/adminUserList"; // templates/admin/adminUserList.html
    }