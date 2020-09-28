package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonRootName("user")
public class UserDTO {
    String id;
    String username;
    String email;
    Integer gender;
    String birthday;
    Boolean registered;
    String profilePhotoUrl;
    String dateRegistered;
    String jwtToken;
    String theme;
    List<String> messageThreadIds;
}
