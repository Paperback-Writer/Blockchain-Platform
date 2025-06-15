package com.book.service;

import com.book.domain.BlockchainData;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class RemoteDataService {
    private final RestTemplate restTemplate;
    
    @Autowired
    public RemoteDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BlockchainData> fetchBlockchainData(String cryptocurrency, String metric) {
        try {
            JSch jsch = new JSch();
            
            // 使用密码认证 - 请替换为实际的服务器信息
            Session session = jsch.getSession("zhongxingdu", "abacus-2.ifi.uzh.ch", 22);
            session.setPassword("my99jsyDu@");
            
            // 配置 SSH 会话
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // 连接
            session.connect();
            
            // 打开 SFTP 通道
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            
            // 远程文件路径
            String remoteFilePath = "/path/to/data/" + cryptocurrency + "/" + metric + ".json";
            
            // 读取文件
            InputStream inputStream = channelSftp.get(remoteFilePath);
            
            // 使用 Jackson 解析 JSON
            ObjectMapper mapper = new ObjectMapper();
            List<BlockchainData> data = mapper.readValue(
                inputStream, 
                new TypeReference<List<BlockchainData>>() {}
            );
            
            // 关闭连接
            channelSftp.disconnect();
            session.disconnect();
            
            return data;
        } catch (Exception e) {
            // 错误处理
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}