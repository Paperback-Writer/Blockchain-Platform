package com.book.service;

import com.book.dao.BlockchainDataDao;
import com.book.domain.BlockchainDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock BlockchainDataService that does not actually access the database
 */
@Service
public class BlockchainDataService {

    private BlockchainDataDao blockchainDataDao;

    @Autowired(required = false) // Set to non-required to avoid startup errors
    public void setBlockchainDataDao(BlockchainDataDao blockchainDataDao) {
        this.blockchainDataDao = blockchainDataDao;
    }

    /**
     * Get chart data (returns mock data)
     */
    public List<Object> getChartData(
            String cryptocurrency, 
            String metric, 
            String analysisType
    ) {
        // Return mock data
        return getMockChartData(cryptocurrency, metric, analysisType);
    }

    /**
     * Batch insert data (does not perform any operation)
     */
    @Transactional
    public void saveBlockchainData(List<BlockchainDataEntity> dataList) {
        // Does not perform any operation
        System.out.println("Mock saving " + dataList.size() + " records");
    }
    
    /**
     * Generate mock data
     */
    private List<Object> getMockChartData(String cryptocurrency, String metric, String analysisType) {
        List<Object> mockData = new ArrayList<>();
        
        // Generate some random data points
        for (int i = 0; i < 10; i++) {
            final int index = i;
            mockData.add(new Object() {
                public double x = index;
                public double y = Math.random() * 100;
            });
        }
        
        return mockData;
    }
}
