package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    CategoryRepository categoryRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PrivateMessageRepository privateMessageRepository;

    @Autowired
    MessageThreadRepository messageThreadRepository;

    // @Scheduled(cron = "[Seconds] [Minutes] [Hours] [Day of month] [Month] [Day of week] [Year]")
    // @Scheduled(cron = "0 * * * * *") // 每分钟一次
    @Scheduled(cron = "0 0 * * * *") // 每小时一次
    private void createTransactionFromSubscription() {

        // DateTime now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0); // 每分钟
        DateTime now = DateTime.now().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0); // 每小时

        System.out.println("now in scheduled task: " + now.toString(ISOFormat));

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
                transactionsCreatedFromSubscription.add(subscriptionService.createTransactionFromSubscription(subscription, now));

                subscription = subscriptionService.getUpdatedSubscriptionAfterTriggered(subscription, now);
                updatedSubscriptions.add(subscription);
            }
        });

        transactionRepository.saveAll(transactionsCreatedFromSubscription);
        subscriptionRepository.saveAll(updatedSubscriptions);
    }

//    @Scheduled(cron = "0 * * * * *") // 每分钟一次
    @Scheduled(cron = "0 30 0 * * *") // 每天00:30
    private void deleteExpiredTempUser() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

        String aWeekAgo = DateTime.now().minusDays(7).toString(fmt);

        List<User> expiredUsers = userRepository.findByDateRegisteredLessThanAndRegisteredIsFalse(aWeekAgo);

        expiredUsers.forEach(user -> {
            List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId());
            List<Category> categories = categoryRepository.findAllByUserId(user.getId());
            List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(user.getId());

            transactionRepository.deleteAll(transactions);
            categoryRepository.deleteAll(categories);
            subscriptionRepository.deleteAll(subscriptions);

            userRepository.delete(user);
        });
    }
}
