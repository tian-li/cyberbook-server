package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    public ResponseEntity<List<Transaction>> getTransactions(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        return ResponseEntity.ok(transactionRepository.findAllByUserId(user.getId()));
    }

    public ResponseEntity<Transaction> getTransactionById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {
            if (user.getId().equals(transaction.get().getUserId())) {
                return ResponseEntity.ok(transaction.get());
            } else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Transaction> addTransaction(TransactionDTO transactionDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);

        String categoryId = transactionDTO.getCategoryId();

        if (categoryId == null || !isCategoryPresent(categoryId)) {
            return new ResponseEntity("Category does not exist", HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();

        transaction.setUserId(user.getId());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setCategoryId(categoryId);
        transaction.setSubscriptionId(transactionDTO.getSubscriptionId());

        transaction.setTransactionDate(DateTime.now().getMillis());
        transaction.setDateModified(DateTime.now().getMillis());
        transaction.setDateCreated(DateTime.now().getMillis());

        return ResponseEntity.ok(transactionRepository.save(transaction));
    }

    public ResponseEntity<Transaction> updateTransaction(String id, TransactionDTO transactionDTO, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> findResult = transactionRepository.findById(id);


        if (findResult.isPresent()) {
            if (user.getId().equals(findResult.get().getUserId())) {
                String categoryId = transactionDTO.getCategoryId();

                if (categoryId == null || !isCategoryPresent(categoryId)) {
                    return new ResponseEntity("Category not exist", HttpStatus.BAD_REQUEST);
                }

                Transaction transaction = findResult.get();
                transaction.setId(id);
                transaction.setAmount(transactionDTO.getAmount());
                transaction.setDescription(transactionDTO.getDescription());
                transaction.setCategoryId(categoryId);
                transaction.setSubscriptionId(transactionDTO.getSubscriptionId());
                transaction.setDateModified(DateTime.now().getMillis());
                return ResponseEntity.ok(transactionRepository.save(transaction));
            } else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity deleteTransactionById(String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> transaction = transactionRepository.findById(id);


        if (transaction.isPresent()) {
            if (user.getId().equals(transaction.get().getUserId())) {
                transactionRepository.deleteById(id);
                return ResponseEntity.ok(id);
            } else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    private Boolean isCategoryPresent(String categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        return category.isPresent();
    }
}
