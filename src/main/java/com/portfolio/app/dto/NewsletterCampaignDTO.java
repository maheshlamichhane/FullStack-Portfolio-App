package com.portfolio.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewsletterCampaignDTO {
    private Long id;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content; // HTML content

    @Size(max = 500, message = "Preview text cannot exceed 500 characters")
    private String previewText;

    private String templateName;

    @NotNull(message = "Profile ID is required")
    private Long profileId;

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;

    private Integer totalRecipients;
    private Integer sentCount;
    private Integer openedCount;
    private Integer clickedCount;

    private String status; // DRAFT, SCHEDULED, SENDING, SENT, CANCELLED

    private List<String> tags;
}
