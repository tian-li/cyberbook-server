package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@ToString
@JsonRootName("transaction")
public class TransactionDTO {
    String id;

    String userId;
    String subscriptionId;
    BigDecimal amount;
    String description;
    String categoryId;

    Long transactionDate;
    Long dateCreated;
    Long dateModified;
}
