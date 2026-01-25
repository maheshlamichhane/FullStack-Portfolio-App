package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationStatsDTO {

    private Long totalCertifications;
    private Long verifiedCertifications;
    private Long expiredCertifications;
    private Long expiringSoonCount; // Within 30 days
    private Long neverExpireCount;
    private Map<String, Long> organizationDistribution;
    private Integer averageValidityMonths;
}
