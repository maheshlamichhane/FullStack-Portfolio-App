package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectImageDTO {
    private Long id;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String altText;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    private Long projectId;
}
