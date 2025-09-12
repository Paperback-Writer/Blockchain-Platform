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
     * Connect to remote server
     * @param host host address
     * @param username username
     * @param password password
     * @throws JSchException SSH connection exception
     */
    public void connect(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, 22);
        session.setPassword(password);
        
        // Disable strict host key checking (use only in development, be cautious in production)
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        
        session.connect();
        System.out.println("Connected to server: " + host);
    }
    
    /**
     * Execute remote command
     * @param command command to execute
     * @return command output
     * @throws JSchException SSH connection exception
     * @throws IOException input/output exception
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
        
        // Wait for command to complete
        while (channel.isConnected()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // If error stream has content, print error message
        if (errorStream.size() > 0) {
            System.out.println("Fail execution " + errorStream.toString());
        }
        
        return outputStream.toString();
    }
    
    /**
     * Download from remote
     * @param remoteFilePath remote file path
     * @param localFilePath local file path
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
