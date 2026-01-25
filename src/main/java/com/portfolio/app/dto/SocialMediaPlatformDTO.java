package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaPlatformDTO {
    private String platform;
    private Long count;
    private String defaultIcon;
    private String exampleUrl;
}
