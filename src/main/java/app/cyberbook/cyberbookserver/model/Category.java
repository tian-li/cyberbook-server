package app.cyberbook.cyberbookserver.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "cyberbook_category")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
public class Category {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @NotBlank(message = "can't be empty")
    private String userId;
    @NotBlank(message = "can't be empty")
    private Integer sortOrder;
    @NotBlank(message = "can't be empty")
    private String name;
    @NotBlank(message = "can't be empty")
    private String icon;
    @NotBlank(message = "can't be empty")
    private String color;
    @NotBlank(message = "can't be empty")
    private Boolean isSpend;
    @NotBlank(message = "can't be empty")
    private Boolean addedByUser;
}
