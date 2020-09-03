package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import app.cyberbook.cyberbookserver.util.BigDecimalUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

@Component
public class ScheduledTasks {

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    @Scheduled(cron = "0 * * * * * ")
    private void createTransactionFromSubscription() {

        DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0);

        System.out.println("now in scheduled task: "+ now.toString(ISOFormat));

        List<Transaction> transactionsCreatedFromSubscription = new ArrayList<>();
        List<Subscription> updatedSubscriptions = new ArrayList<>();

        List<Subscription> subscriptions = subscriptionRepository.findAllByActivateStatusIsTrue();

        subscriptions.forEach(subscription -> {
            if (!userRepository.existsById(subscription.getUserId())) {
                subscription.setActivateStatus(false);
                subscription.setEndDate(DateTime.now().toString(ISOFormat));
                subscription.setDateModified(DateTime.now().toString(ISOFormat));
                updatedSubscriptions.add(subscription);
                return;
            }

            // if end date is same or before now (not after now), should set it to inactive and not add transaction
            if (subscription.getEndDate() != null && !DateTime.parse(subscription.getEndDate()).isAfter(now)) {
                subscription.setActivateStatus(false);
                updatedSubscriptions.add(subscription);
                return;
            }

            DateTime originalNextDate = DateTime.parse(subscription.getNextDate());

            if (!originalNextDate.isAfter(now)) {
                transactionsCreatedFromSubscription.add(transactionService.createTransactionFromSubscription(subscription, now));

                String updatedNextDate;
                Integer period = subscription.getPeriod();

                switch (subscription.getFrequency()) {
                    case 1:
                        updatedNextDate = now.plusDays(period).toString(ISOFormat);
                        break;
                    case 2:
                        updatedNextDate = now.plusWeeks(period).toString(ISOFormat);
                        break;
                    case 3:
                        updatedNextDate = now.plusMonths(period).toString(ISOFormat);
                        break;
                    case 4:
                        updatedNextDate = now.plusYears(period).toString(ISOFormat);
                        break;
                    case 5:
                        updatedNextDate = now.plusMinutes(period).toString(ISOFormat);
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
                updatedSubscriptions.add(subscription);
            }
        });

        transactionRepository.saveAll(transactionsCreatedFromSubscription);
        subscriptionRepository.saveAll(updatedSubscriptions);
    }
}
