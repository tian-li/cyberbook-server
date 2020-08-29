package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    public ResponseEntity<CyberbookServerResponse<List<Subscription>>> getSubscriptions(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        return ResponseEntity.ok(CyberbookServerResponse.successWithData(subscriptionRepository.findAllByUserId(user.getId())));
    }

    public ResponseEntity<CyberbookServerResponse<Subscription>> getSubscriptionById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Subscription> subscription = subscriptionRepository.findById(id);

        if (subscription.isPresent()) {
            if (user.getId().equals(subscription.get().getUserId())) {
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(subscription.get()));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CyberbookServerResponse<Subscription>> addSubscription(SubscriptionDTO subscriptionDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);

        String categoryId = subscriptionDTO.getCategoryId();

        if (categoryId == null || !categoryService.isCategoryPresent(categoryId)) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Category does not exist"), HttpStatus.BAD_REQUEST);
        }

        Subscription subscription = new Subscription();

        subscription.setUserId(user.getId());
        subscription.setAmount(subscriptionDTO.getAmount());
        subscription.setDescription(subscriptionDTO.getDescription());
        subscription.setCategoryId(categoryId);

        subscription.setFrequency(subscriptionDTO.getFrequency());
        subscription.setPeriod(subscriptionDTO.getPeriod());
        subscription.setSummary(subscriptionDTO.getSummary());
        subscription.setTotalAmount(subscriptionDTO.getTotalAmount());

        subscription.setStartDate(new DateTime(subscriptionDTO.getStartDate()).getMillis());
        subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).getMillis());
        subscription.setNextDate(new DateTime(subscriptionDTO.getNextDate()).getMillis());

        subscription.setDateModified(DateTime.now().getMillis());
        subscription.setDateCreated(DateTime.now().getMillis());

        return ResponseEntity.ok(CyberbookServerResponse.successWithData(subscriptionRepository.save(subscription)));
    }

    public ResponseEntity<CyberbookServerResponse<Subscription>> updateSubscription(String id, SubscriptionDTO subscriptionDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Subscription> findResult = subscriptionRepository.findById(id);


        if (findResult.isPresent()) {
            if (user.getId().equals(findResult.get().getUserId())) {
                String categoryId = subscriptionDTO.getCategoryId();

                if (categoryId == null || !categoryService.isCategoryPresent(categoryId)) {
                    return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Category does not exist"), HttpStatus.BAD_REQUEST);
                }

                Subscription subscription = findResult.get();
                subscription.setId(id);
                subscription.setAmount(subscriptionDTO.getAmount());
                subscription.setDescription(subscriptionDTO.getDescription());
                subscription.setCategoryId(categoryId);
                subscription.setDateModified(DateTime.now().getMillis());

                subscription.setFrequency(subscriptionDTO.getFrequency());
                subscription.setPeriod(subscriptionDTO.getPeriod());
                subscription.setSummary(subscriptionDTO.getSummary());
                subscription.setTotalAmount(subscriptionDTO.getTotalAmount());

                subscription.setStartDate(new DateTime(subscriptionDTO.getStartDate()).getMillis());
                subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).getMillis());
                subscription.setNextDate(new DateTime(subscriptionDTO.getNextDate()).getMillis());

                return ResponseEntity.ok(CyberbookServerResponse.successWithData(subscriptionRepository.save(subscription)));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity deleteSubscriptionById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Subscription> subscription = subscriptionRepository.findById(id);


        if (subscription.isPresent()) {
            if (user.getId().equals(subscription.get().getUserId())) {
                subscriptionRepository.deleteById(id);
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(id));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public Boolean isSubscriptionPresent(String subscriptionId) {
        Optional<Subscription> subscription = subscriptionRepository.findById(subscriptionId);

        return subscription.isPresent();
    }
}
