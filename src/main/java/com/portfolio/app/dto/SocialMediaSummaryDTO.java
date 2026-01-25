package com.portfolio.app.dto;


import lombok.Data;

@Data
public class SocialMediaSummaryDTO {
    private Long id;
    private String platform;
    private String url;
    private String username;
    private String iconClass;
    private Integer displayOrder;
    private Boolean isVisible;
}
