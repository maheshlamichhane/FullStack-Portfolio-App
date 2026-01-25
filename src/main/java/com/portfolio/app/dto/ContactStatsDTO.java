package com.portfolio.app.dto;

package com.portfolio.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactStatsDTO {
    private Long totalMessages;
    private Long unreadMessages;
    private Long archivedMessages;
    private Long todayMessages;
    private Long weekMessages;
}
