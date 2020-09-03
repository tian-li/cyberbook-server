package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    public ResponseEntity<CyberbookServerResponse<List<Transaction>>> getTransactions(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        return ResponseEntity.ok(CyberbookServerResponse.successWithData(transactionRepository.findAllByUserId(user.getId())));
    }

    public ResponseEntity<CyberbookServerResponse<Transaction>> getTransactionById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {
            if (user.getId().equals(transaction.get().getUserId())) {
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(transaction.get()));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CyberbookServerResponse<Transaction>> addTransaction(TransactionDTO transactionDTO, HttpServletRequest req) {
        DateTime now = DateTime.now();
        User user = userService.getUserByHttpRequestToken(req);

        String categoryId = transactionDTO.getCategoryId();
        String subscriptionId = transactionDTO.getSubscriptionId();

        if (categoryId == null || !categoryService.isCategoryPresent(categoryId)) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Category does not exist"), HttpStatus.BAD_REQUEST);
        }

        if (subscriptionId != null && !subscriptionService.isSubscriptionPresent(subscriptionId)) {
            return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Subscription does not exist"), HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();

        transaction.setUserId(user.getId());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setCategoryId(categoryId);
        transaction.setSubscriptionId(subscriptionId);

        transaction.setTransactionDate(now.toString(ISOFormat));
        transaction.setDateModified(now.toString(ISOFormat));
        transaction.setDateCreated(now.toString(ISOFormat));

        return ResponseEntity.ok(CyberbookServerResponse.successWithData(transactionRepository.save(transaction)));
    }

    public ResponseEntity<CyberbookServerResponse<Transaction>> updateTransaction(String id, TransactionDTO transactionDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> findResult = transactionRepository.findById(id);

        if (findResult.isPresent()) {
            if (user.getId().equals(findResult.get().getUserId())) {
                String categoryId = transactionDTO.getCategoryId();
                String subscriptionId = transactionDTO.getSubscriptionId();

                if (categoryId == null || !categoryService.isCategoryPresent(categoryId)) {
                    return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Category does not exist"), HttpStatus.BAD_REQUEST);
                }

                if (subscriptionId != null && !subscriptionService.isSubscriptionPresent(subscriptionId)) {
                    return new ResponseEntity<>(CyberbookServerResponse.noDataMessage("Subscription does not exist"), HttpStatus.BAD_REQUEST);

                }

                Transaction transaction = findResult.get();
                transaction.setId(id);
                transaction.setAmount(transactionDTO.getAmount());
                transaction.setDescription(transactionDTO.getDescription());
                transaction.setCategoryId(categoryId);
                transaction.setSubscriptionId(subscriptionId);
                transaction.setDateModified(DateTime.now().toString(ISOFormat));
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(transactionRepository.save(transaction)));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity deleteTransactionById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> transaction = transactionRepository.findById(id);


        if (transaction.isPresent()) {
            if (user.getId().equals(transaction.get().getUserId())) {
                transactionRepository.deleteById(id);
                return ResponseEntity.ok(CyberbookServerResponse.successWithData(id));
            } else {
                return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.NOT_FOUND);
        }
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
