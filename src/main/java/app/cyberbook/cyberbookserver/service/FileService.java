package app.cyberbook.cyberbookserver.service;

import app.cyberbook.cyberbookserver.model.CyberbookServerResponse;
import app.cyberbook.cyberbookserver.util.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public ResponseEntity<CyberbookServerResponse<String>> upload(
            MultipartFile file,
            String extensionName,
            String tempLocalPath,
            String remotePath,
            String imageToDelete
    ) {
        return doUpload(file, extensionName, tempLocalPath, remotePath, imageToDelete);
    }

    public ResponseEntity<CyberbookServerResponse<String>> upload(
            MultipartFile file,
            String tempLocalPath,
            String remotePath,
            String imageToDelete
    ) {
        String fileName = file.getOriginalFilename();

        if(fileName!=null) {
            String extensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
            return doUpload(file, extensionName, tempLocalPath, remotePath, imageToDelete);
        } else {
            return new ResponseEntity<>(CyberbookServerResponse.failedWithData("上传文件失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<CyberbookServerResponse<String>> doUpload(
            MultipartFile file,
            String extensionName,
            String tempLocalPath,
            String remotePath,
            String imageToDelete

    ) {
        String uploadFileName = UUID.randomUUID() + "." + extensionName;

        logger.info("开始上传文件: {}", uploadFileName);

        File fileDir = new File(tempLocalPath);
        logger.info("fileDir.exists: {}", fileDir.exists());

        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(tempLocalPath, uploadFileName);

        try {
            file.transferTo(targetFile);
            // 此时文件已经上传成功

            // 将 targetFile 上传到 ftp
            FTPUtil.uploadFile(Lists.newArrayList(targetFile), remotePath, imageToDelete);

            // 此时已经上传到ftp

            // 上传完之后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return new ResponseEntity<>(CyberbookServerResponse.failedWithData("上传文件失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(CyberbookServerResponse.successWithData(targetFile.getName()), HttpStatus.OK);
    }
}
