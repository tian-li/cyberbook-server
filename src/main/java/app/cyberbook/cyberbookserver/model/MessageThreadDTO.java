package app.cyberbook.cyberbookserver.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonRootName("message_thread")
public class MessageThreadDTO {
    String id;
    int type = MessageType.PRIVATE.getCode();
    String preview;
    String lastMessageDate;
}
