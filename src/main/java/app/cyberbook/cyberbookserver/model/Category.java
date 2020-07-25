package app.cyberbook.cyberbookserver.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
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

//    public Category(String id, String userId, Integer sortOrder, String name, String icon, String color, Const.TransactionType type, Boolean addedByUser) {
//        this.id = id;
//        this.userId = userId;
//        this.sortOrder = sortOrder;
//        this.name = name;
//        this.icon = icon;
//        this.color = color;
//        this.type = type;
//        this.addedByUser = addedByUser;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public Integer getSortOrder() {
//        return sortOrder;
//    }
//
//    public void setSortOrder(Integer sortOrder) {
//        this.sortOrder = sortOrder;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getIcon() {
//        return icon;
//    }
//
//    public void setIcon(String icon) {
//        this.icon = icon;
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }
//
//    public Const.TransactionTypes getType() {
//        return type;
//    }
//
//    public void setType(Const.TransactionTypes type) {
//        this.type = type;
//    }
//
//    public Boolean getAddedByUser() {
//        return addedByUser;
//    }
//
//    public void setAddedByUser(Boolean addedByUser) {
//        this.addedByUser = addedByUser;
//    }
}
