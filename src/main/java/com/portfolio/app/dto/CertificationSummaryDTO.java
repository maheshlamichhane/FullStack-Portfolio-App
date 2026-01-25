package com.portfolio.app.dto;


import lombok.Data;
import java.time.LocalDate;

@Data
public class CertificationSummaryDTO {
    private Long id;
    private String name;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private Boolean doesNotExpire;
    private Boolean isVerified;
    private Boolean isExpired;
    private String logoUrl;
}
