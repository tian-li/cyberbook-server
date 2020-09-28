package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.PrivateMessage;
import app.cyberbook.cyberbookserver.model.PrivateMessageDTO;
import app.cyberbook.cyberbookserver.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/api/private-message")
public class PrivateMessageController {

    @Autowired
    PrivateMessageService privateMessageService;

    @PostMapping(path = "feedback")
    public ResponseEntity<CyberbookServerResponse<PrivateMessage>> giveFeedback(@RequestBody PrivateMessageDTO privateMessageDTO, HttpServletRequest req) {
        return privateMessageService.addFeedback(privateMessageDTO, req);
    }

    @GetMapping(path = "message-thread/{messageThreadId}")
    public ResponseEntity<CyberbookServerResponse<List<PrivateMessage>>> getPrivateMessageListByMessageThreadId(@PathVariable String messageThreadId, HttpServletRequest req) {
        return privateMessageService.getPrivateMessageListByMessageThreadId(messageThreadId, req);
    }
}
