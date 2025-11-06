package com.pgc.localinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") // 사용자가 http://localhost:8081/ 주소로 접속하면
    public String home() {
        return "home"; // "home.html" 파일을 찾아서 보여달라는 의미
    }
}