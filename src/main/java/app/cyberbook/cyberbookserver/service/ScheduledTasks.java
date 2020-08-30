package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

@Component
public class ScheduledTasks {
//    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

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

//    @Scheduled(cron = "0 * * * * * ")
    private void createTransactionFromSubscription() {
        System.out.println("createTransactionFromSubscription running");

        DateTime now = DateTime.now();


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

//            logger.info();

            DateTime originalNextDate = DateTime.parse(subscription.getNextDate());

            if (!originalNextDate.isAfter(now)) {
                transactionsCreatedFromSubscription.add(transactionService.createTransactionFromSubscription(subscription));

                String updatedNextDate;
                Integer period = subscription.getPeriod();

                switch (subscription.getFrequency()) {
                    case 0:
                        updatedNextDate = originalNextDate.plusDays(period).toString(ISOFormat);
                        break;
                    case 1:
                        updatedNextDate = originalNextDate.plusWeeks(period).toString(ISOFormat);
                        break;
                    case 2:
                        updatedNextDate = originalNextDate.plusMonths(period).toString(ISOFormat);
                        break;
                    case 3:
                        updatedNextDate = originalNextDate.plusYears(period).toString(ISOFormat);
                        break;
                    case 4:
                        updatedNextDate = originalNextDate.plusMinutes(period).toString(ISOFormat);
                        break;
                    default:
                        updatedNextDate = subscription.getNextDate();
                        break;
                }

//                if ()

                subscription.setNextDate(updatedNextDate);
                subscription.setDateModified(DateTime.now().toString(ISOFormat));
                updatedSubscriptions.add(subscription);
            }
        });

        transactionRepository.saveAll(transactionsCreatedFromSubscription);
        subscriptionRepository.saveAll(updatedSubscriptions);

    }
}
