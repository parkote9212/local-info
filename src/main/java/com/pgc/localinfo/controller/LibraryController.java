package com.pgc.localinfo.controller;

import com.pgc.localinfo.dto.LibraryDto;
import com.pgc.localinfo.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.util.List; // 1. java.util.List 임포트

@Controller
@RequiredArgsConstructor
@RequestMapping("/libraries")
public class LibraryController {

    private final LibraryService libraryService;

    @Value("${kakao.map.app-key}")
    private String kakaoMapAppKey;

    @GetMapping("")
    public String libraryList(Model model) {

        // 1. API 호출 (비동기 Flux 반환)
        Flux<LibraryDto> libraryFlux = libraryService.getLibraries(1, 100);

        // 2. [수정] 비동기 Flux를 -> 동기(Blocking) List로 변환
        // .collectList() : 모든 데이터를 리스트로 모음 (Mono<List<LibraryDto>>)
        // .block() : API 호출이 끝날 때까지 여기서 '대기'한 후, List<LibraryDto>를 반환
        List<LibraryDto> libraryList = libraryFlux.collectList().block();

        // 3. [수정] Flux가 아닌, 완성된 List를 모델에 추가
        model.addAttribute("libraries", libraryList);
        model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);

        // (이제 "libraries/list.html"의 th:each는 List<LibraryDto>를 순회하게 됩니다)
        return "libraries/list";
    }

    /**
     * [추가] 도서관 상세 페이지 (GET)
     * @param address (필수) 도서관 주소
     * @param name    (필수) 도서관 이름
     * @param model   뷰에 데이터를 전달
     * @return
     */
    @GetMapping("/detail")
    public String libraryDetail(@RequestParam String address,
                                @RequestParam String name,
                                @RequestParam String lat,
                                @RequestParam String lon,
                                Model model) {

        // 1. URL 파라미터로 받은 도서관 정보를 Model에 담아 뷰로 전달
        // (이 정보는 '팁 작성' 시 API로 다시 보내야 함)
        model.addAttribute("libraryName", name);
        model.addAttribute("libraryAddress", address);
        model.addAttribute("libraryLat", lat);
        model.addAttribute("libraryLon", lon);
        model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);

        // 2. 뷰 반환
        return "libraries/detail"; // templates/libraries/detail.html
    }
}