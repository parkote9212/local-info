package com.pgc.localinfo.service;

import com.pgc.localinfo.dto.LibraryApiResponse;
import com.pgc.localinfo.dto.LibraryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType; // [추가]
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final WebClient gyeonggiLibraryWebClient;

    @Value("${api.key.public-data}")
    private String apiKey;

    public Flux<LibraryDto> getLibraries(int pageIndex, int pageSize) {

        return gyeonggiLibraryWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("KEY", apiKey)
                        // [삭제] .queryParam("Type", "json")
                        .queryParam("pIndex", pageIndex)
                        .queryParam("pSize", pageSize)
                        .build())
//                .accept(MediaType.APPLICATION_XML) // [수정] XML을 요청
                .retrieve()
                .bodyToMono(LibraryApiResponse.class) // XML을 LibraryApiResponse DTO로 변환
                .flatMapMany(response -> {
                    // [수정] DTO 구조가 'row'만 포함하도록 변경됨
                    if (response != null && response.getRow() != null) {
                        return Flux.fromIterable(response.getRow());
                    } else {
                        return Flux.empty();
                    }
                })
                .onErrorResume(e -> {
                    // XML 파싱 실패 시 여기로 옴
                    System.err.println("API 호출 또는 XML 파싱 오류: " + e.getMessage());
                    return Flux.empty();
                });
    }
}