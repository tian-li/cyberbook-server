package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.Subscription;
import app.cyberbook.cyberbookserver.model.SubscriptionDTO;
import app.cyberbook.cyberbookserver.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/subscriptions")
public class SubscriptionController {
    @Autowired
    SubscriptionService subscriptionService;

    @GetMapping()
    public ResponseEntity<CyberbookServerResponse<List<Subscription>>> getSubscriptions(HttpServletRequest req) {
        return subscriptionService.getSubscriptions(req);
    }

    @PostMapping()
    public ResponseEntity<CyberbookServerResponse<Subscription>> addSubscription(@Valid @RequestBody SubscriptionDTO subscriptionDTO, HttpServletRequest req) {
        return subscriptionService.addSubscription(subscriptionDTO, req);
    }

    @PostMapping(path = "stop/{id}")
    public ResponseEntity<CyberbookServerResponse<Subscription>> stopSubscription(@PathVariable("id") String id, @RequestBody SubscriptionDTO subscriptionDTO, HttpServletRequest req) {
        return subscriptionService.stopSubscription(id, subscriptionDTO, req);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<CyberbookServerResponse<Subscription>> getSubscriptionById(@PathVariable("id") String id, HttpServletRequest req) {
        return subscriptionService.getSubscriptionById(id, req);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<CyberbookServerResponse<Subscription>> updateSubscription(@PathVariable("id") String id, @RequestBody SubscriptionDTO subscriptionDTO, HttpServletRequest req) {
        return subscriptionService.updateSubscription(id, subscriptionDTO, req);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteSubscriptionById(@PathVariable("id") String id, HttpServletRequest req) {
        return subscriptionService.deleteSubscriptionById(id, req);
    }
}
