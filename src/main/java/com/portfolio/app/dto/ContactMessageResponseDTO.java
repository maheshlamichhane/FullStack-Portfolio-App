package com.portfolio.app.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
//@SuperBuilder
public class ContactMessageResponseDTO extends ContactMessageDTO {
    private String profileName;
    private String profileEmail;
}
