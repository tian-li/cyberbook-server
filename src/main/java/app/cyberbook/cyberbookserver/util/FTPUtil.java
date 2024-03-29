package app.cyberbook.cyberbookserver.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static final String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static final String ftpPass = PropertiesUtil.getProperty("ftp.pass");
    private static final String ftpRootFolder = PropertiesUtil.getProperty("ftp.rootFolder");

    public FTPUtil(String ip, int port, String user, String pwd, String rootFolder) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.rootFolder = rootFolder;
    }

    public static boolean uploadFile(
            List<File> fileList,
            String remotePath,
            String imageToDelete
    ) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass, ftpRootFolder);
        logger.info("开始连接 ftp 服务器 ip: {}, remotePath:{}， ftpRootFolder: {}", ftpIp, remotePath, ftpRootFolder);
        boolean result = ftpUtil.uploadFile(remotePath, fileList, imageToDelete);
        logger.info("开始连接 ftp 服务器，结束上传，上传结果：{}", result);
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList, String imageToDelete) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        // 连接 FTP 服务器
        if (connectServer(this.ip, this.port, this.user, this.pwd)) {
            try {
                ftpClient.changeWorkingDirectory(ftpRootFolder+"/"+remotePath);
                int returnCode = ftpClient.getReplyCode();
                if (returnCode == 550) {
                    logger.info("开始新建文件夹 remotePath: {}", remotePath);
                    boolean mkdir = ftpClient.makeDirectory(remotePath);

                    if(!mkdir) {
                        throw new IOException("FTP新建文件夹失败");
                    }
                    ftpClient.changeWorkingDirectory(ftpRootFolder);
                }

                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }

                if (imageToDelete != null) {
                    ftpClient.deleteFile(imageToDelete);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常", e);
                e.printStackTrace();
            } finally {
                // 上传完成后，释放连接
                fis.close();
                ftpClient.disconnect();
            }
        } else {
            return false;
        }
        return uploaded;
    }

    private boolean connectServer(String ip, int port, String user, String pwd) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("登录FTP异常", e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private String rootFolder;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}