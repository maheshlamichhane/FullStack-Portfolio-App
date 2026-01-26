package com.portfolio.app.dto;


import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Data
//@SuperBuilder
public class SubscriberResponseDTO extends SubscriberDTO {
    private String profileName;
    private String profileTitle;
    private Long daysSubscribed;
}
