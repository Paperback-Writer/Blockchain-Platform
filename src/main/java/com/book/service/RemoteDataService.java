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
            
            // Use password authentication - replace with actual server information
            Session session = jsch.getSession("zhongxingdu", "abacus-2.ifi.uzh.ch", 22);
            session.setPassword("my99jsyDu@");
            
            // Configure SSH session
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // Connect
            session.connect();
            
            // Open SFTP channel
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            
            // Remote file path
            String remoteFilePath = "/path/to/data/" + cryptocurrency + "/" + metric + ".json";
            
            // Read file
            InputStream inputStream = channelSftp.get(remoteFilePath);
            
            // Parse JSON using Jackson
            ObjectMapper mapper = new ObjectMapper();
            List<BlockchainData> data = mapper.readValue(
                inputStream, 
                new TypeReference<List<BlockchainData>>() {}
            );
            
            // Close connections
            channelSftp.disconnect();
            session.disconnect();
            
            return data;
        } catch (Exception e) {
            // Error handling
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
