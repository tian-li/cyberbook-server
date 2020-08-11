package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserRepository;
import app.cyberbook.cyberbookserver.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping(path = "registerTempUser")
    @ResponseBody
    public ResponseEntity<User> registerTempUser() {
        User user = new User();

        user.setUsername("mock username");
        user.setBirthday(null);
        user.setEmail(null);
        user.setGender(null);
        user.setProfilePhotoUrl(null);
        user.setRegistered(false);
        user.setDateRegistered(DateTime.now().getMillis());

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping(path = "login")
    @ResponseBody
    public ResponseEntity login(@RequestBody User value) {
        System.out.println("login user"+value);
        return userService.login(value);
    }

    @GetMapping(path = "list")
    @ResponseBody
    public ResponseEntity list() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping(path = "register")
    @ResponseBody
    public ResponseEntity register(@Valid @RequestBody User value) {
        return userService.register(value);
    }
}
