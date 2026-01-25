package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CertificationResponseDTO extends CertificationDTO {
    private String profileName;
    private String profileTitle;
    private Boolean isExpired;
    private Integer validityMonths;
    private Integer daysUntilExpiration;
}
