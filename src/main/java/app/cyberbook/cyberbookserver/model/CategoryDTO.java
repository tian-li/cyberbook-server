package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@JsonRootName("category")
public class CategoryDTO {
    String id;
    @NotBlank(message = "can't be empty")
    String userId;
    @NotBlank(message = "can't be empty")
    Integer sortOrder;
    @NotBlank(message = "can't be empty")
    String name;
    @NotBlank(message = "can't be empty")
    String icon;
    @NotBlank(message = "can't be empty")
    String color;
    @NotBlank(message = "can't be empty")
    Boolean isSpend;
    @NotBlank(message = "can't be empty")
    Boolean addedByUser;
}
