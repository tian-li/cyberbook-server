package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.MessageThread;
import app.cyberbook.cyberbookserver.model.PrivateMessage;
import app.cyberbook.cyberbookserver.model.PrivateMessageDTO;
import app.cyberbook.cyberbookserver.service.MessageThreadService;
import app.cyberbook.cyberbookserver.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/api/message-threads")
public class MessageThreadController {

    @Autowired
    MessageThreadService messageThreadService;

    @GetMapping()
    public ResponseEntity<CyberbookServerResponse<List<MessageThread>>> getMessageThreads(HttpServletRequest req) {
        return messageThreadService.getMessageThreads(req);
    }

//    @GetMapping(path = "/{messageThreadId}")
//    public ResponseEntity<CyberbookServerResponse<List<PrivateMessage>>> getPrivateMessageListByMessageThreadId(@PathVariable String messageThreadId, HttpServletRequest req) {
//        return privateMessageService.getPrivateMessageListByMessageThreadId(messageThreadId, req);
//    }
}
