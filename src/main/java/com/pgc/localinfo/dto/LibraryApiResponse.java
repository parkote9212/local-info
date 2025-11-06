package com.pgc.localinfo.dto;

// [삭제] Jackson 관련 import 모두 삭제
import lombok.Getter;
import lombok.Setter;
import java.util.List;

// [추가] JAXB 어노테이션 임포트
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


@Getter
@Setter
// [추가] JAXB가 필드 기준으로 XML을 매핑하도록 설정
@XmlAccessorType(XmlAccessType.FIELD)
// [수정] 1. 최상위 XML 태그 이름
@XmlRootElement(name = "TBGGIBLLBR")
public class LibraryApiResponse {

    @XmlElement(name = "head") // 2. <head> 태그
    private Object head;

    @XmlElement(name = "row") // 3. <row> 태그 (JAXB는 List<Dto>를 자동으로 처리)
    private List<LibraryDto> row;
}