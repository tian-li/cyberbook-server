package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.MessageThread;
import app.cyberbook.cyberbookserver.model.MessageThreadDTO;
import app.cyberbook.cyberbookserver.model.MessageThreadRepository;
import app.cyberbook.cyberbookserver.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageThreadService {
    @Autowired
    MessageThreadRepository messageThreadRepository;

    @Autowired
    UserService userService;

//    public MessageThread

    public List<MessageThread> getMessageThreadListByUserId(String userId) {
       return messageThreadRepository.findAllByUsers(userService.getUserById(userId));
    }

    public boolean userHasMessageThreadId(String userId, String messageThreadId) {
        return getMessageThreadListByUserId(userId).stream().map(MessageThread::getId).collect(Collectors.toList()).contains(messageThreadId);
    }

    public MessageThread createMessageThread(MessageThreadDTO messageThreadDTO) {
        MessageThread messageThread = new MessageThread();

        List<User> users = messageThreadDTO.getUserIds().stream().map((userId)-> userService.getUserById(userId)).collect(Collectors.toList());

        messageThread.setUsers(users);
        messageThread.setPreview(messageThreadDTO.getPreview());
        messageThread.setType(messageThreadDTO.getType());

        return messageThread;
    }

    public MessageThread save(MessageThread messageThread) {
        return messageThreadRepository.save(messageThread);
    }
}