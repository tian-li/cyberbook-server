package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

public class PrivateMessageService {
    @Autowired
    MessageThreadRepository messageThreadRepository;
    @Autowired
    MessageThreadService messageThreadService;
    @Autowired
    private PrivateMessageRepository privateMessageRepository;
    @Autowired
    private UserService userService;

    public ResponseEntity<CyberbookServerResponse<List<PrivateMessage>>> getPrivateMessageListByMessageThreadId(
            String messageThreadId,
            HttpServletRequest req
    ) {
        User tokenUser = userService.getUserByHttpRequestToken(req);

        boolean userHasMessageThreadId = messageThreadService.userHasMessageThreadId(tokenUser.getId(), messageThreadId);

        if (userHasMessageThreadId) {
            try {
                List<PrivateMessage> privateMessageList = privateMessageRepository.findAllByMessageThreadId(messageThreadId);
                return new ResponseEntity<>(CyberbookServerResponse.successWithData(privateMessageList), HttpStatus.OK);
            } catch (Error e) {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("没有此消息记录"), HttpStatus.FORBIDDEN);
        }

    }

    public ResponseEntity<CyberbookServerResponse<PrivateMessage>> addFeedback(PrivateMessageDTO privateMessageDTO, HttpServletRequest req) {
        User fromUser = userService.getUserByHttpRequestToken(req);
        User toUser = userService.getFeedbackManagerUser();
        return addPrivateMessage(privateMessageDTO, fromUser, toUser);
    }

    public ResponseEntity<CyberbookServerResponse<PrivateMessage>> addPrivateMessage(
            PrivateMessageDTO privateMessageDTO,
            User fromUser,
            User toUser) {


        if (fromUser == null || toUser == null) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("User does not exist"), HttpStatus.BAD_REQUEST);
        }

        String now = DateTime.now().toString(ISOFormat);

        try {
            PrivateMessage privateMessage = addNewMessage(privateMessageDTO, fromUser, toUser, MessageType.PRIVATE.getCode(), now);
            return new ResponseEntity<>(CyberbookServerResponse.successWithData(privateMessage), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("发送消息失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public PrivateMessage addNewMessage(
            PrivateMessageDTO privateMessageDTO,
            User fromUser,
            User toUser,
            Integer type,
            String messageDate
    ) {

        String messageThreadId = privateMessageDTO.getMessageThreadId();
        MessageThread messageThread;
        if (messageThreadId == null) {
            messageThread = messageThreadService.createMessageThread(new MessageThreadDTO());
        } else {
            messageThread = messageThreadRepository.findById(messageThreadId).orElse(messageThreadService.createMessageThread(new MessageThreadDTO()));
        }

        PrivateMessage privateMessage = new PrivateMessage();

        privateMessage.setFromUserId(fromUser.getId());
        privateMessage.setToUserId(toUser.getId());
        privateMessage.setMessage(privateMessageDTO.getMessage());
        privateMessage.setDateCreated(messageDate);

        List<User> users = messageThread.getUsers();
        users.add(fromUser);
        users.add(toUser);

        messageThread.setUsers(users);
        messageThread.setPreview(privateMessageDTO.getMessage().substring(0, 15));
        messageThread.setLastMessageDate(messageDate);
        messageThread.setType(type);

        try {
            MessageThread savedMessageThread = messageThreadService.save(messageThread);
            privateMessage.setMessageThreadId(savedMessageThread.getId());
            userService.saveTreadToUser(savedMessageThread, fromUser);
            userService.saveTreadToUser(savedMessageThread, toUser);
            return privateMessageRepository.save(privateMessage);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

//    private MessageThread createMessageThread(
//            User fromUser,
//            User toUser,
//            PrivateMessageDTO privateMessageDTO,
//            String now,
//            int type
//    ) {
////        MessageThread messageThread = new MessageThread();
//        MessageThreadDTO messageThreadDTO = new MessageThreadDTO();
//
//        List<String> userIds = new ArrayList<>();
//        userIds.add(fromUser.getId());
//        userIds.add(toUser.getId());
//
//        messageThreadDTO.setUserIds(userIds);
//        messageThreadDTO.setPreview(privateMessageDTO.getMessage().substring(0, 15));
//        messageThreadDTO.setLastMessageDate(now);
//        messageThreadDTO.setType(type);
//
//        return messageThreadService.createMessageThread(messageThreadDTO);
//    }

//    private void saveTreadToUser(String threadId, User user) {
//        List<String> existingThreadIds = user.getThreadIds();
//        if (existingThreadIds == null) {
//            existingThreadIds = new ArrayList<>();
//        }
//        existingThreadIds.add(threadId);
//    }
}
