package com.example;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * sftp工具类
 *
 * @version V2.1
 * @since 2.1.0 2019/5/9 10:06
 */
public final class SftpUtil {

    public static final String directory = "/hsdata/sftpfile/xd2-1";
    private static final Logger log = LoggerFactory.getLogger(SftpUtil.class);
    private static ChannelSftp sftp;
    private static SftpUtil instance = null;
    private String host;
    private int port;
    private String username;
    private String password;

    private SftpUtil() {
        this.host = "192.168.160.40";
        this.port = 22;
        this.username = "root";
        this.password = "123";
    }

    private SftpUtil(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public static SftpUtil getInstance() {
        if (instance == null) {
            instance = new SftpUtil();
            sftp = instance.connect();   //获取连接
        }
        return instance;
    }

    public static SftpUtil getInstance(String host, int port, String username, String password) {
        if (instance == null) {
            instance = new SftpUtil(host, port, username, password);
            sftp = instance.connect();   //获取连接
        }
        return instance;
    }

    public static void main(String[] args) throws IOException {
        SftpUtil sf = SftpUtil.getInstance();
        sf.upload(directory, "C:\\Users\\Q\\Desktop\\test102.txt");    //上传文件

        //        sf.download(directory, "2.png", "C:\\Users\\hp\\Desktop\\1212.png");
        // File download = sf.download("/home/1.png", "C:\\Users\\hp\\Desktop\\122221.png");

        // sf.delete(directory, "test102.txt"); //删除文件

        /*Vector<ChannelSftp.LsEntry> files = null;        //查看文件列表
        try {
            files = sf.listFiles(directory);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        for (ChannelSftp.LsEntry file : files) {
            System.out.println("###\t" + file.getFilename());
        }*/
        sf.disconnect();
    }

    /**
     * 连接sftp服务器
     *
     * @return
     */
    public ChannelSftp connect() {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(this.username, this.host, this.port);
            Session sshSession = jsch.getSession(this.username, this.host, this.port);
            sshSession.setPassword(this.password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            log.info("SFTP Session connected.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            log.info("Connected to " + this.host);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sftp;
    }

    /**
     * 上传文件
     *
     * @param directory 上传的目录
     * @param uploadFile 要上传的文件
     */
    public String upload(String directory, String uploadFile) {
        try {
            sftp.cd(directory);
            File file = new File(uploadFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            sftp.put(fileInputStream, file.getName());
            fileInputStream.close();
            return directory + File.separator + file.getName();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String upload(String directory, String sftpFileName, byte[] byteArr) {
        try {
            sftp.cd(directory);
            sftp.put(new ByteArrayInputStream(byteArr), sftpFileName);
            return directory + File.separator + sftpFileName;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String upLoadFile(String sftpFileName, InputStream input) {
        try {
            sftp.put(input, sftpFileName);
        } catch (Exception var7) {
            log.error("上传sftp文件异常", var7);
        } finally {
            this.disconnect();
        }

        return directory + File.separator + sftpFileName;
    }

    /**
     * 下载文件
     *
     * @param directory 下载目录
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     */
    public File download(String directory, String downloadFile, String saveFile) {
        try {
            sftp.cd(directory);
            File file = new File(saveFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            sftp.get(downloadFile, fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 下载文件
     *
     * @param downloadFilePath 下载的文件完整目录
     * @param saveFile 存在本地的路径
     */
    public File download(String downloadFilePath, String saveFile) {
        try {
            int i = downloadFilePath.lastIndexOf('/');
            if (i == -1)
                return null;
            sftp.cd(downloadFilePath.substring(0, i));
            File file = new File(saveFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            sftp.get(downloadFilePath.substring(i + 1), fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 删除文件
     *
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            sftp.getSession()
                .disconnect();
        } catch (JSchException e) {
            log.error(e.getMessage());
        }
        sftp.quit();
        sftp.disconnect();
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @throws SftpException
     */
    public Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }
}
