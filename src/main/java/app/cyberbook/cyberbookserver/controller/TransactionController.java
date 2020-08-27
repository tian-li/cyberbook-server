package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.*;
import app.cyberbook.cyberbookserver.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<Transaction>> getTransactions(HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        List<Transaction> transactionList = transactionRepository.findAllByUserId(user.getId());
//        transactionList.forEach(c -> System.out.println("find by user id" + c));
        return ResponseEntity.ok(transactionList);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent() && user.getId().equals(transaction.get().getUserId())) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping()
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDTO value, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);

        String categoryId = value.getCategoryId();

        if (categoryId == null || !isCategoryPresent(categoryId)) {
            return new ResponseEntity("Category not exist", HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();

        transaction.setUserId(user.getId());
        transaction.setAmount(value.getAmount());
        transaction.setDescription(value.getDescription());
        transaction.setCategoryId(categoryId);
        transaction.setSubscriptionId(value.getSubscriptionId());

        transaction.setTransactionDate(DateTime.now().getMillis());
        transaction.setDateModified(DateTime.now().getMillis());
        transaction.setDateCreated(DateTime.now().getMillis());

        return ResponseEntity.ok(transactionRepository.save(transaction));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") String id, @RequestBody TransactionDTO changes, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> findResult = transactionRepository.findById(id);

        if (findResult.isPresent() && user.getId().equals(findResult.get().getUserId())) {
            String categoryId = changes.getCategoryId();

            if (categoryId == null || !isCategoryPresent(categoryId)) {
                return new ResponseEntity("Category not exist", HttpStatus.BAD_REQUEST);
            }

            Transaction transaction = findResult.get();
            transaction.setId(id);
            transaction.setAmount(changes.getAmount());
            transaction.setDescription(changes.getDescription());
            transaction.setCategoryId(categoryId);
            transaction.setSubscriptionId(changes.getSubscriptionId());
            transaction.setDateModified(DateTime.now().getMillis());
            return ResponseEntity.ok(transactionRepository.save(transaction));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteTransactionById(@PathVariable("id") String id, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent() && user.getId().equals(transaction.get().getUserId())) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok(id);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    private Boolean isCategoryPresent(String categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        return category.isPresent();
    }

    // TODO
//    private Boolean checkSubscription(String categoryId) {
//        Optional<Category> category =  categoryRepository.findById(categoryId);
//
//        return category.isEmpty();
//    }
}
