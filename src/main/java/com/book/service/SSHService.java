package com.book.service;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Service
public class SSHService {

    private Session session = null;
    private ChannelExec channel = null;
    
    /**
     * 连接到远程服务器
     * @param host 主机地址
     * @param username 用户名
     * @param password 密码
     * @throws JSchException SSH连接异常
     */
    public void connect(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, 22);
        session.setPassword(password);
        
        // 禁用严格主机密钥检查（开发环境使用，生产环境应当谨慎）
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        
        session.connect();
        System.out.println("已连接到服务器: " + host);
    }
    
    /**
     * 执行远程命令
     * @param command 要执行的命令
     * @return 命令输出内容
     * @throws JSchException SSH连接异常
     * @throws IOException 输入输出异常
     */
    public String executeCommand(String command) throws JSchException, IOException {
        System.out.println("execute command " + command);
        
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        
        channel.setOutputStream(outputStream);
        channel.setErrStream(errorStream);
        
        channel.connect();
        
        // 等待命令完成
        while (channel.isConnected()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 如果错误流中有内容，则打印错误信息
        if (errorStream.size() > 0) {
            System.out.println("Fail execution " + errorStream.toString());
        }
        
        return outputStream.toString();
    }
    
    /**
     * download from remote
     * @param remoteFilePath 
     * @param localFilePath 
     * @throws JSchException 
     * @throws SftpException 
     */
    public void downloadFile(String remoteFilePath, String localFilePath) throws JSchException, SftpException {
        System.out.println("download - remote path: " + remoteFilePath + ", local path: " + localFilePath);
        
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        
        try {
            File localFile = new File(localFilePath);
            File parentDir = localFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (dirCreated) {
                    System.out.println("create path: " + parentDir.getAbsolutePath());
                }
            }
            
            channelSftp.get(remoteFilePath, localFilePath);
            System.out.println("Success!");
        } finally {
            channelSftp.disconnect();
        }
    }
    
    public void disconnect() {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("Disconnect SSH");
        }
    }
}