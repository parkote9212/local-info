package com.pgc.localinfo.dto;

// [삭제] Jackson 관련 import 모두 삭제
import lombok.Getter;
import lombok.Setter;

// [추가] JAXB 어노테이션 임포트
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@Getter
@Setter
// [추가] JAXB가 필드 기준으로 XML을 매핑하도록 설정
@XmlAccessorType(XmlAccessType.FIELD)
public class LibraryDto {

    // [수정] 모든 어노테이션을 @XmlElement(name = "...")로 변경
    @XmlElement(name = "SIGUN_NM")
    private String sigunName;

    @XmlElement(name = "LIBRRY_NM")
    private String libraryName;

    @XmlElement(name = "LOCPLC_ADDR")
    private String address;

    @XmlElement(name = "TELNO")
    private String telNo;

    @XmlElement(name = "HMPG_ADDR")
    private String homepageUrl;

    @XmlElement(name = "REFINE_WGS84_LAT")
    private String latitude;

    @XmlElement(name = "REFINE_WGS84_LOGT")
    private String longitude;
}