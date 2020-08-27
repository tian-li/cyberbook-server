package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserRepository;
import app.cyberbook.cyberbookserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping(path = "registerTempUser")
    public ResponseEntity registerTempUser() {
        return userService.registerTempUser();
    }

    @PostMapping(path = "login")
    public ResponseEntity login(@RequestBody User value) {
        return userService.login(value);
    }

    @GetMapping(path = "list")
    public ResponseEntity list() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping(path = "register")
    public ResponseEntity register(@Valid @RequestBody User value, HttpServletRequest req) {
        return userService.register(value, req);
    }
}
