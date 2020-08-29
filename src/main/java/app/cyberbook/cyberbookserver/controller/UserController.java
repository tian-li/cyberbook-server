package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserDTO;
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

    @PostMapping(path = "loginWithToken")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> loginWithToken(HttpServletRequest req) {
        return  userService.loginWithToken(req);
    }

    @PostMapping(path = "registerTempUser")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> registerTempUser() {
        return userService.registerTempUser();
    }

    @PostMapping(path = "login")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> login(@RequestBody User value) {
        return userService.login(value);
    }

    @PostMapping(path = "register")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> register(@RequestBody User value, HttpServletRequest req) {
        return userService.register(value, req);
    }
}
