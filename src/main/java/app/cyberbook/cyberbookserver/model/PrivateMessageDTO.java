package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonRootName("private_message")
public class PrivateMessageDTO {
    String id;
    String fromUserId;
    String toUserId;
    String message;
    String messageThreadId;
    String dateCreated;
}
