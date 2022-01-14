package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.Role;
import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.model.UserDTO;
import app.cyberbook.cyberbookserver.service.UserCreationService;
import app.cyberbook.cyberbookserver.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserCreationService userCreationService;

    @PostMapping(path = "login-with-token")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> loginWithToken(HttpServletRequest req) {
        return userService.loginWithToken(req);
    }

    @PostMapping(path = "register-temp-user")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> registerTempUser() {
        return userCreationService.registerTempUser();
    }

    @PostMapping(path = "save-temp-user")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> saveTempUser(@RequestBody User value, HttpServletRequest req) {
        return userService.saveTempUser(value, req);
    }

    @PostMapping(path = "login")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> login(@RequestBody User value) {
        return userService.login(value);
    }

    @PostMapping(path = "register-feedback-manager")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> registerWithRole(@RequestBody User value) {
        return userCreationService.register(value, Role.ROLE_FEEDBACK_MANAGER);
    }

    @PostMapping(path = "register")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> register(@RequestBody User value) {
        return userCreationService.register(value, Role.ROLE_CLIENT);
    }

    @PostMapping(path = "set-theme")
    public ResponseEntity<CyberbookServerResponse> setTheme(@RequestBody String theme, HttpServletRequest req) {
        return userService.setTheme(theme, req);
    }

    @PutMapping(path = "update-profile")
    public ResponseEntity<CyberbookServerResponse<UserDTO>> updateProfile(@RequestBody User value, HttpServletRequest req) {
        return userService.updateProfile(value, req);
    }

    @GetMapping(path = "timezone")
    public ResponseEntity<String> getTimezone() {
        DateTime now = DateTime.now();
        StringBuilder sb = new StringBuilder();

        sb.append("Timezone: " + now.getZone().toString())
                .append("; Time: " + now.toString(ISOFormat));


        return ResponseEntity.ok(sb.toString());

    }
}
