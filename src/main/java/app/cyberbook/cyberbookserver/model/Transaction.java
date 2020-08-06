package app.cyberbook.cyberbookserver.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cyberbook_transaction")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
public class Transaction {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String userId;
    private BigDecimal amount;
    private String description;
    private String categoryId;
    private String subscriptionId;

    //    @Temporal(TemporalType.TIMESTAMP)
//    @CreationTimestamp
    private Long transactionDate;
    //    @Temporal(TemporalType.TIMESTAMP)
//    @CreationTimestamp
    private Long dateCreated;
    //    @Temporal(TemporalType.TIMESTAMP)
//    @CreationTimestamp
    private Long dateModified;
}
