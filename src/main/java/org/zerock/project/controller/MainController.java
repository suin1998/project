package org.zerock.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/community")
    public String communityList() {
        return "community/usercommunity";
    }

    @GetMapping("/AICoordinator")
    public String aiCoordinator() {
        return "Ai/AICoordinator";
    }

    @GetMapping("/MyPage")
    public String myPage() {
        return "user/myPage";
    }

}