package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

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
        subscription.setTotalAmount(0);

        subscription.setStartDate(new DateTime(subscriptionDTO.getStartDate()).toString(ISOFormat));
        subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).toString(ISOFormat));
        subscription.setNextDate(new DateTime(subscriptionDTO.getNextDate()).toString(ISOFormat));

        subscription.setDateModified(DateTime.now().toString(ISOFormat));
        subscription.setDateCreated(DateTime.now().toString(ISOFormat));

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
                subscription.setAmount(subscriptionDTO.getAmount());
                subscription.setDescription(subscriptionDTO.getDescription());
                subscription.setCategoryId(categoryId);
                subscription.setDateModified(DateTime.now().toString(ISOFormat));

                subscription.setFrequency(subscriptionDTO.getFrequency());
                subscription.setPeriod(subscriptionDTO.getPeriod());
                subscription.setSummary(subscriptionDTO.getSummary());

                subscription.setStartDate(new DateTime(subscriptionDTO.getStartDate()).toString(ISOFormat));
                subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).toString(ISOFormat));
                subscription.setNextDate(new DateTime(subscriptionDTO.getNextDate()).toString(ISOFormat));

                return ResponseEntity.ok(CyberbookServerResponse.successWithData(subscriptionRepository.save(subscription)));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CyberbookServerResponse<Subscription>> stopSubscription(String id, SubscriptionDTO subscriptionDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Subscription> findResult = subscriptionRepository.findById(id);

        if (findResult.isPresent()) {
            if (user.getId().equals(findResult.get().getUserId())) {

                Subscription subscription = findResult.get();

                // Need to get end data on client to make it at correct time in local time zone
                // Because backend is set to UTC.
                // Otherwise the end date time will always be 00:00:000 of UTC time,
                // which may not be start of day of client local time zone
                subscription.setDateModified(DateTime.now().toString(ISOFormat));
                subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).toString(ISOFormat));

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
