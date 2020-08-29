package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.Transaction;
import app.cyberbook.cyberbookserver.model.TransactionDTO;
import app.cyberbook.cyberbookserver.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @GetMapping()
    public ResponseEntity<CyberbookServerResponse<List<Transaction>>> getTransactions(HttpServletRequest req) {
        return transactionService.getTransactions(req);
    }

    @PostMapping()
    public ResponseEntity<CyberbookServerResponse<Transaction>> addTransaction(@Valid @RequestBody TransactionDTO transactionDTO, HttpServletRequest req) {
        return transactionService.addTransaction(transactionDTO, req);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<CyberbookServerResponse<Transaction>> getTransactionById(@PathVariable("id") String id, HttpServletRequest req) {
        return transactionService.getTransactionById(id, req);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<CyberbookServerResponse<Transaction>> updateTransaction(@PathVariable("id") String id, @RequestBody TransactionDTO transactionDTO, HttpServletRequest req) {
        return transactionService.updateTransaction(id, transactionDTO, req);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteTransactionById(@PathVariable("id") String id, HttpServletRequest req) {
        return transactionService.deleteTransactionById(id, req);
    }
}
