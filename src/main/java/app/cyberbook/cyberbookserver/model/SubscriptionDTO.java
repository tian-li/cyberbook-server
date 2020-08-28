package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@JsonRootName("subscription")
public class SubscriptionDTO {
    String id;

    String userId;
    BigDecimal amount;
    String description;
    Integer frequency;
    Integer period;
    String categoryId;
    String summary;
    Integer totalAmount;

    Long startDate;
    Long endDate;
    Long dateCreated;
    Long dateModified;
    Long nextDate;
}
