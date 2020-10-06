package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageThreadService {
    @Autowired
    MessageThreadRepository messageThreadRepository;

    @Autowired
    UserService userService;

    public ResponseEntity<CyberbookServerResponse<List<MessageThread>>> getMessageThreads(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);

        return ResponseEntity.ok(CyberbookServerResponse.successWithData(user.getMessageThreads()));
//        return null;
    }

    public List<MessageThread> getMessageThreadListByUserId(String userId) {
//       return messageThreadRepository.findAllByUsers(userService.getUserById(userId));
        return null;
    }

    public boolean userHasMessageThreadId(String userId, String messageThreadId) {
        User user = userService.getUserById(userId);

        if(user==null) {
            return false;
        } else {
            return user.getMessageThreads().stream().map(MessageThread::getId).collect(Collectors.toList()).contains(messageThreadId);
        }
    }

    public MessageThread createMessageThread(MessageThreadDTO messageThreadDTO) {
        MessageThread messageThread = new MessageThread();

//        List<User> users = messageThreadDTO.getUserIds().stream().map((userId)-> userService.getUserById(userId)).collect(Collectors.toList());

//        messageThread.setUsers(users);
        messageThread.setPreview(messageThreadDTO.getPreview());
        messageThread.setType(messageThreadDTO.getType());

        return messageThread;
    }

    public MessageThread save(MessageThread messageThread) {
        return messageThreadRepository.save(messageThread);
    }
}