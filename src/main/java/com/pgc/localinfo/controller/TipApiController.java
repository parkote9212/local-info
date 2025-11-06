package com.pgc.localinfo.controller;

import com.pgc.localinfo.dto.TipCreateRequestDto;
import com.pgc.localinfo.dto.TipResponseDto;
import com.pgc.localinfo.dto.TipUpdateRequestDto;
import com.pgc.localinfo.security.UserDetailsImpl;
import com.pgc.localinfo.service.TipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tips") // '/api/tips'로 시작하는 요청 담당
public class TipApiController {

    private final TipService tipService;

    /**
     * (팁 생성 C) AJAX POST 요청 처리
     * @param requestDto 팁 생성 DTO (JSON)
     * @param userDetails 로그인한 사용자
     * @return 성공(200), 유효성 검사 실패(400), 서버 오류(500)
     */
    @PostMapping
    public ResponseEntity<String> createTip(@Valid @RequestBody TipCreateRequestDto requestDto,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 1. (보안) 로그인 여부 확인
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 2. (유효성 검사) DTO의 @NotBlank, @Size 등 검사
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bindingResult.getFieldError().getDefaultMessage()); // 첫 번째 오류 메시지 반환
        }

        try {
            // 3. (TDD 검증 완료) 서비스 로직 호출
            tipService.createTip(userDetails.getUsername(), requestDto);
            return ResponseEntity.ok("팁 등록 성공"); // 200 OK

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("팁 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * (팁 조회 R) AJAX GET 요청 처리
     * @param libraryAddress 팁 목록을 조회할 도서관 주소 (Query Parameter)
     * @param userDetails (선택) 로그인한 사용자 (본인 글 표시용)
     * @return 팁 DTO 목록 (JSON)
     */
    @GetMapping
    public ResponseEntity<List<TipResponseDto>> getTips(@RequestParam String libraryAddress,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 'isOwner' 필드 계산을 위해 로그인한 사용자 ID 전달
        String username = (userDetails != null) ? userDetails.getUsername() : null;

        // (TDD 검증 완료) 서비스 로직 호출
        List<TipResponseDto> tips = tipService.getTipsByLibraryAddress(libraryAddress, username);

        return ResponseEntity.ok(tips); // 200 OK와 함께 DTO 리스트 반환
    }
    @PutMapping("/{tipId}")
    public ResponseEntity<String> updateTip(@PathVariable Long tipId,
                                            @Valid @RequestBody TipUpdateRequestDto requestDto,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // ... (수정 로직) ...
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bindingResult.getFieldError().getDefaultMessage());
        }
        try {
            tipService.updateTip(tipId, requestDto.getContent(), userDetails.getUsername());
            return ResponseEntity.ok("팁 수정 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("팁 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * (팁 삭제 D) AJAX DELETE 요청 처리
     */
    @DeleteMapping("/{tipId}")
    public ResponseEntity<String> deleteTip(@PathVariable Long tipId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // ... (삭제 로직) ...
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            tipService.deleteTip(tipId, userDetails.getUsername());
            return ResponseEntity.ok("팁 삭제 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}