package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SocialMediaDTO {
    private Long id;

    @NotBlank(message = "Platform is required")
    @Size(min = 2, max = 50, message = "Platform must be between 2 and 50 characters")
    private String platform; // GitHub, LinkedIn, Twitter, etc.

    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$",
            message = "Please provide a valid URL")
    @Size(max = 500, message = "URL cannot exceed 500 characters")
    private String url;

    @Size(max = 100, message = "Username cannot exceed 100 characters")
    private String username;

    @Size(max = 100, message = "Icon class cannot exceed 100 characters")
    private String iconClass; // For font awesome or similar

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    private Boolean isVisible = true;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
