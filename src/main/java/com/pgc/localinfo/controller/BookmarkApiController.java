package com.pgc.localinfo.controller;

import com.pgc.localinfo.dto.BookmarkRequestDto;
import com.pgc.localinfo.security.UserDetailsImpl;
import com.pgc.localinfo.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkApiController {

    private final BookmarkService bookmarkService;

    /**
     * (찜하기) AJAX POST 요청 처리
     * @param requestDto 찜할 도서관 정보 (JSON)
     * @param userDetails 로그인한 사용자 정보
     * @return 성공/실패 응답
     */
    @PostMapping
    public ResponseEntity<String> addBookmark(@RequestBody BookmarkRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                    .body("로그인이 필요합니다.");
        }

        try {
            // 5. TDD로 검증된 서비스 로직 호출
            String username = userDetails.getUsername();
            bookmarkService.addBookmark(username, requestDto);

            // 6. 성공 시
            return ResponseEntity.ok("찜하기 성공"); // 200 OK

        } catch (IllegalStateException e) {
            // 7. (실패) 찜하기 서비스에서 "중복 찜" 예외가 발생한 경우
            return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(e.getMessage()); // "이미 찜한 도서관입니다."
        } catch (Exception e) {
            // 8. (실패) 그 외 알 수 없는 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body("찜하기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * @param id 삭제할 bookmark_id
     * @param userDetails 로그인한 사용자 정보
     * @return 성공/실패 응답
     */
    @DeleteMapping("/{id}") // 예: DELETE /api/bookmarks/10
    public ResponseEntity<String> deleteBookmark(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        try {
            // TDD로 검증된 서비스 로직 호출 (소유권 검증 포함)
            bookmarkService.deleteBookmark(id, userDetails.getUsername());

            return ResponseEntity.ok("찜 삭제 성공"); // 200 OK

        } catch (IllegalArgumentException e) {
            // "삭제 권한이 없거나..." 예외가 발생한 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
