package app.cyberbook.cyberbookserver.controller;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.model.User;
import app.cyberbook.cyberbookserver.service.FileService;
import app.cyberbook.cyberbookserver.service.UserService;
import app.cyberbook.cyberbookserver.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/image")
public class ImageController {
    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

//    @GetMapping({"id"})
//    public ResponseEntity<CyberbookServerResponse<> getCategories(HttpServletRequest req) {
//        return fileService.getCategories(req);
//    }

    @PostMapping("upload")
    public ResponseEntity<CyberbookServerResponse<String>> upload(MultipartFile file, HttpServletRequest req) {
        User user = userService.getUserByHttpRequestToken(req);

        if (user == null) {
            return new ResponseEntity<>(CyberbookServerResponse.failedNoData(), HttpStatus.FORBIDDEN);
        }

        String tempLocalPath = req.getSession().getServletContext().getRealPath("upload");

        System.out.println("tempLocalPath: " + tempLocalPath);

        return fileService.upload(file, tempLocalPath, user.getId());
    }
}
