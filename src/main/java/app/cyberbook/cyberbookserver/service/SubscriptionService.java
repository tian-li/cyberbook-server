package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import app.cyberbook.cyberbookserver.util.BigDecimalUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private CategoryService categoryService;

//    @Autowired
//    private TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

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

        DateTime startDate = new DateTime(subscriptionDTO.getStartDate());
        DateTime dateCreated = new DateTime(subscriptionDTO.getDateCreated());

        Subscription subscription = new Subscription();

        subscription.setUserId(user.getId());
        subscription.setAmount(subscriptionDTO.getAmount());
        subscription.setDescription(subscriptionDTO.getDescription());
        subscription.setCategoryId(categoryId);

        subscription.setFrequency(subscriptionDTO.getFrequency());
        subscription.setPeriod(subscriptionDTO.getPeriod());
        subscription.setSummary(subscriptionDTO.getSummary());
        subscription.setTotalAmount(new BigDecimal(0));
        subscription.setActivateStatus(true);

        subscription.setStartDate(startDate.toString(ISOFormat));

        if (subscriptionDTO.getEndDate() != null) {
            subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).toString(ISOFormat));
        } else {
            subscription.setEndDate(null);
        }

        subscription.setNextDate(new DateTime(subscriptionDTO.getNextDate()).toString(ISOFormat));

        subscription.setDateModified(DateTime.now().toString(ISOFormat));
        subscription.setDateCreated(DateTime.now().toString(ISOFormat));

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // if start date and create date are the same day
        if (startDate.toLocalDate().isEqual(dateCreated.toLocalDate())) {
            Transaction transaction = createTransactionFromSubscription(savedSubscription, startDate);
            transactionRepository.save(transaction);

            savedSubscription = getUpdatedSubscriptionAfterTriggered(savedSubscription, startDate);
            subscriptionRepository.save(savedSubscription);
        }

        return ResponseEntity.ok(CyberbookServerResponse.successWithData(savedSubscription));
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

                if (subscriptionDTO.getEndDate() != null) {
                    subscription.setEndDate(new DateTime(subscriptionDTO.getEndDate()).toString(ISOFormat));
                    if (!new DateTime(subscriptionDTO.getEndDate()).isAfter(DateTime.now())) {
                        subscription.setActivateStatus(false);
                    }
                }

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
                subscription.setActivateStatus(false);

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

    public Subscription getUpdatedSubscriptionAfterTriggered(Subscription subscription, DateTime triggerTime) {
        String updatedNextDate;
        Integer period = subscription.getPeriod();

        switch (subscription.getFrequency()) {
            case 1:
                updatedNextDate = triggerTime.plusDays(period).toString(ISOFormat);
                break;
            case 2:
                updatedNextDate = triggerTime.plusWeeks(period).toString(ISOFormat);
                break;
            case 3:
                updatedNextDate = triggerTime.plusMonths(period).toString(ISOFormat);
                break;
            case 4:
                updatedNextDate = triggerTime.plusYears(period).toString(ISOFormat);
                break;
            default:
                updatedNextDate = subscription.getNextDate();
                break;
        }

        subscription.setNextDate(updatedNextDate);
        subscription.setDateModified(DateTime.now().toString(ISOFormat));
        BigDecimal updatedTotalAmount = BigDecimalUtil.add(
                subscription.getTotalAmount().doubleValue(),
                subscription.getAmount().doubleValue()
        );

        subscription.setTotalAmount(updatedTotalAmount);
        return subscription;
    }

    public Transaction createTransactionFromSubscription(Subscription subscription, DateTime now) throws RuntimeException {
        if (subscription.getCategoryId() == null || !categoryService.isCategoryPresent(subscription.getCategoryId())) {
            throw new RuntimeException("Category does not exist");
        }

        Transaction transaction = new Transaction();

        transaction.setUserId(subscription.getUserId());
        transaction.setAmount(subscription.getAmount());
        transaction.setDescription(subscription.getDescription());
        transaction.setCategoryId(subscription.getCategoryId());
        transaction.setSubscriptionId(subscription.getId());

        transaction.setTransactionDate(now.toString(ISOFormat));
        transaction.setDateCreated(now.toString(ISOFormat));
        transaction.setDateModified(now.toString(ISOFormat));

        return transaction;
    }
}
