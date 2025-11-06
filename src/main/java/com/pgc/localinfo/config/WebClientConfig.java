package com.pgc.localinfo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.url.gyeonggi-library}")
    private String gyeonggiLibraryApiUrl;

    @Bean
    public WebClient gyeonggiLibraryWebClient() {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().jaxb2Decoder(
                                // [수정] 2번째 인수였던 MediaType.APPLICATION_XML 제거
                                new Jaxb2XmlDecoder()
                        )
                ).build();

        return WebClient.builder()
                .baseUrl(gyeonggiLibraryApiUrl)
                .exchangeStrategies(strategies)
                .build();
    }
}