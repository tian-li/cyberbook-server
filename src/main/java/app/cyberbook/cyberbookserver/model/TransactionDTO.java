package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@ToString
@JsonRootName("transaction")
public class TransactionDTO {
    String id;

    @NotBlank(message = "can't be empty") String userId;
    String subscriptionId;
    @NotBlank(message = "can't be empty") BigDecimal amount;
    String description;
    @NotBlank(message = "can't be empty") String categoryId;

    Long transactionDate;
    Long dateCreated;
    Long dateModified;
}
