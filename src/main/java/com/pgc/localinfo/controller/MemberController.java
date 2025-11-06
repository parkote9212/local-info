package com.pgc.localinfo.controller;

import com.pgc.localinfo.dto.MemberSignupRequestDto;
import com.pgc.localinfo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute("memberSignupRequestDto", new MemberSignupRequestDto());
        // [수정] 뷰 경로 수정
        return "members/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberSignupRequestDto requestDto,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // [수정] 뷰 경로 수정
            return "members/signup";
        }

        try {
            memberService.signup(requestDto);
        } catch (IllegalStateException e) {
            bindingResult.addError(new org.springframework.validation.FieldError("memberSignupRequestDto", "username", e.getMessage()));
            // [수정] 뷰 경로 수정
            return "members/signup";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(){ // 메서드명 오타 수정 (loginfrom -> loginForm)
        // [수정] 뷰 경로 수정
        return "members/login";
    }
}
