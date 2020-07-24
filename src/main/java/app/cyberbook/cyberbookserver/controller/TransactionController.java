package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping()
    @ResponseBody
    public ResponseEntity<List<Transaction>> getCategories(@RequestParam(name = "userId") String userId) {
        List<Transaction> transactionList = transactionRepository.findAllByUserId(userId);
        transactionList.forEach(c -> System.out.println("find by user id" + c));
        return ResponseEntity.ok(transactionList);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Optional<Transaction>> getTransactionById(@PathVariable("id") String id) {
        return ResponseEntity.ok(transactionRepository.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDTO value) {
        System.out.println("value"+value.toString());
        String categoryId = value.getCategoryId();

        System.out.println("categoryId"+categoryId);

        if(categoryId == null || !isCategoryPresent(categoryId)) {
            return new ResponseEntity("Category not exist", HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction();

        transaction.setUserId(value.getUserId());
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
    public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") String id, @RequestBody TransactionDTO changes) {
        String categoryId = changes.getCategoryId();
        if(categoryId == null || !isCategoryPresent(categoryId)) {
            return new ResponseEntity("Category not exist", HttpStatus.BAD_REQUEST);
        }
        // TODO check subscription


        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUserId(changes.getUserId());
        transaction.setAmount(changes.getAmount());
        transaction.setDescription(changes.getDescription());
        transaction.setCategoryId(categoryId);
        transaction.setSubscriptionId(changes.getSubscriptionId());
        transaction.setDateModified(DateTime.now().getMillis());
        return ResponseEntity.ok(transactionRepository.save(transaction));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity.BodyBuilder deleteTransactionById(@PathVariable("id") String id) {
        try {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok();
        } catch (Exception e) {
            return ResponseEntity.badRequest();
        }
    }

    private Boolean isCategoryPresent(String categoryId) {
        Optional<Category> category =  categoryRepository.findById(categoryId);

        return category.isPresent();
    }

    // TODO
//    private Boolean checkSubscription(String categoryId) {
//        Optional<Category> category =  categoryRepository.findById(categoryId);
//
//        return category.isEmpty();
//    }
}
