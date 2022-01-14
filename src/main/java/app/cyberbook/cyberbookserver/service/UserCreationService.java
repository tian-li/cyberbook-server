package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.*;
import app.cyberbook.cyberbookserver.security.JwtTokenProvider;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static app.cyberbook.cyberbookserver.model.Const.ISOFormat;

public class UserCreationService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<CyberbookServerResponse<UserDTO>> registerTempUser() {
        User user = new User();

        user.setUsername("新用户" + UUID.randomUUID().toString().substring(0, 4));
        user.setBirthday(null);
        user.setEmail(null);
        user.setGender(null);
        user.setProfilePhoto(null);
        user.setRegistered(false);
        user.setDateRegistered(DateTime.now().toString(ISOFormat));

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);
        userRepository.save(user);

        try {
            List<CategoryDTO> categoryDTOs = categoryService.generateDefaultCategoryDTOs();
            categoryService.createCategories(categoryDTOs, user.getId());
        } catch (IOException e) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Error when creating default categories when creating temp user"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(UserDTO.createUserDTOWithUserAndToken(
                        user,
                        jwtTokenProvider.createToken(user.getUsernameOrEmail(), user.getRoles())
                ))
        );
    }

    public ResponseEntity<CyberbookServerResponse<UserDTO>> register(User value, Role role) {
        if (value == null || value.getUsername() == null || value.getEmail() == null || value.getPassword() == null) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Invalid register info"), HttpStatus.BAD_REQUEST);
        }
        User user = new User();

        if (userRepository.existsByUsername(value.getUsername())) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Username is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userRepository.existsByEmail(value.getEmail())) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Email is already in use"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        user.setUsername(value.getUsername());
        user.setBirthday(null);
        user.setEmail(value.getEmail());
        user.setGender(0);
        user.setProfilePhoto(null);
        user.setRegistered(true);
        user.setDateRegistered(DateTime.now().toString(ISOFormat));

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(value.getPassword()));

        userRepository.save(user);

        try {
            List<CategoryDTO> categoryDTOs = categoryService.generateDefaultCategoryDTOs();
            categoryService.createCategories(categoryDTOs, user.getId());
        } catch (IOException e) {
            return new ResponseEntity(CyberbookServerResponse.noDataMessage("Error when creating default categories"), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return ResponseEntity.ok(
                CyberbookServerResponse.successWithData(UserDTO.createUserDTOWithUserAndToken(
                        user,
                        jwtTokenProvider.createToken(user.getUsernameOrEmail(), user.getRoles())
                ))
        );
    }
}
