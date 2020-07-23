package app.cyberbook.cyberbookserver.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

    @GetMapping()
    public Integer getTransactions() {
        return 123;
    }

    @GetMapping(path = "{id}")
    public Integer getTransactionById(@PathVariable("id") String id) {
        return 123;
    }

    @PostMapping()
    public Integer createTransaction(@RequestBody String value) {
        return 123;
    }

    @PutMapping(path = "{id}")
    public Integer updateTransaction(@PathVariable("id") String id, @RequestBody String value) {
        return 123;
    }

    @DeleteMapping(path = "{id}")
    public Integer deleteTransactionById(@PathVariable("id") String id) {
        return 123;
    }
}
