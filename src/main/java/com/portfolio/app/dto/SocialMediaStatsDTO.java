package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaStatsDTO {
    private Long totalSocialLinks;
    private Long visibleLinks;
    private Long hiddenLinks;
    private Map<String, Long> platformDistribution;
    private Integer mostUsedPlatformCount;
    private String mostUsedPlatform;
}
