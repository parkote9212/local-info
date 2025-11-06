package com.pgc.localinfo.controller;

import com.pgc.localinfo.dto.BookmarkResponseDto;
import com.pgc.localinfo.security.UserDetailsImpl;
import com.pgc.localinfo.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final BookmarkService bookmarkService;

    @GetMapping
    public String mypageHome(@AuthenticationPrincipal UserDetailsImpl userDetails,
                         Model model){
        String username = userDetails.getUsername();

        List<BookmarkResponseDto> bookmarks = bookmarkService.getMyBookmarks(username);

        model.addAttribute("bookmarks", bookmarks);
        model.addAttribute("nickname", userDetails.getMember().getNickname());

        return "members/mypage";
    }
}
