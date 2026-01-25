package com.portfolio.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberStatsDTO {
    private Long totalSubscribers;
    private Long activeSubscribers;
    private Long confirmedSubscribers;
    private Long unconfirmedSubscribers;
    private Long unsubscribedCount;
    private Long todaySubscriptions;
    private Long weekSubscriptions;
    private Long monthSubscriptions;
    private Map<String, Long> sourceDistribution;
    private Double growthRate; // Percentage growth
}
