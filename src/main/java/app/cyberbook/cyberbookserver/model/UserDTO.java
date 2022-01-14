package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

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
    String profilePhoto;
    String dateRegistered;
    String jwtToken;
    String theme;
    List<MessageThreadDTO> messageThreads;

    static public UserDTO createUserDTOWithUserAndToken(User user, String jwtToken) {
        UserDTO userDTO = new UserDTO();

        List<MessageThreadDTO> messageThreadDTOList = user.getMessageThreads().stream().map(messageThread -> {
            MessageThreadDTO messageThreadDTO = new MessageThreadDTO();

            messageThreadDTO.setLastMessageDate(messageThread.getLastMessageDate());
            messageThreadDTO.setPreview(messageThread.getPreview());
            messageThreadDTO.setId(messageThread.getId());
            messageThreadDTO.setType(messageThread.getType());

            return messageThreadDTO;
        }).collect(Collectors.toList());

        userDTO.setBirthday(user.getBirthday());
        userDTO.setDateRegistered(user.getDateRegistered());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender());
        userDTO.setId(user.getId());
        userDTO.setProfilePhoto(user.getProfilePhoto());
        userDTO.setRegistered(user.getRegistered());
        userDTO.setUsername(user.getUsername());
        userDTO.setTheme(user.getTheme());
        userDTO.setJwtToken(jwtToken);
        userDTO.setMessageThreads(messageThreadDTOList);

        return userDTO;
    }
}
