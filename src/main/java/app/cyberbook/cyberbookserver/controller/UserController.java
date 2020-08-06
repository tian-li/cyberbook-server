package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

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

    @GetMapping(path = "list")
    @ResponseBody
    public ResponseEntity list() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping(path = "register")
    @ResponseBody
    public ResponseEntity register(@Valid @RequestBody User value) {
        if (value == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity("Invalid register info", HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userRepository.findById(value.getId());

        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();

            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setRegistered(true);
        } else {
            user = new User();

            user.setBirthday(null);
            user.setEmail(value.getEmail());
            user.setPassword(value.getPassword());
            user.setGender(null);
            user.setProfilePhotoUrl(null);
            user.setRegistered(true);
            user.setDateRegistered(DateTime.now().getMillis());
        }
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
