package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.service.FileService;
import app.cyberbook.cyberbookserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/api/image")
public class ImageController {
    private final Logger logger = LoggerFactory.getLogger(ImageController.class);
    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @PostMapping("upload")
    public ResponseEntity<CyberbookServerResponse<String>> upload(
            MultipartFile file,
            String extensionName,
            String role,
            HttpServletRequest req
    ) {
        User user = userService.getUserByHttpRequestToken(req);

        if (user == null) {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
        }

        String imageToDelete = null;

        if (role.equals("Profile")) {
            imageToDelete = user.getProfilePhoto();
        }

        String tempLocalPath = req.getSession().getServletContext().getRealPath("upload");

        logger.info("extensionName: {}", extensionName);

        return fileService.upload(file, extensionName, tempLocalPath, user.getId(), imageToDelete);
    }
}
