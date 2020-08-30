package app.cyberbook.cyberbookserver.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cyberbook_subscription")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
public class Subscription {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String userId;
    private BigDecimal amount;
    private String description;
    private Integer frequency;
    private Integer period;
    private String categoryId;
    private String summary;
    private Integer totalAmount;

    private String startDate;
    private String endDate;
    private String dateCreated;
    private String dateModified;
    private String nextDate;

    private Boolean activateStatus;
}
