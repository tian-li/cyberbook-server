package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonRootName("category")
public class CategoryDTO {
    String id;
    String userId;
    Integer sortOrder;
    String name;
    String icon;
    String color;
    Boolean isSpend;
    Boolean addedByUser;
}
