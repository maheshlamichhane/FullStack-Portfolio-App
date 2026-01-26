package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
//@SuperBuilder
public class SocialMediaResponseDTO extends SocialMediaDTO {
    private String profileName;
    private String profileTitle;
    private String platformIcon; // Auto-generated based on platform
}
