package com.book.service;

import com.book.dao.BlockchainDataDao;
import com.book.domain.BlockchainDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟的BlockchainDataService，不会真正访问数据库
 */
@Service
public class BlockchainDataService {

    private BlockchainDataDao blockchainDataDao;

    @Autowired(required = false) // 设置为非必须，避免启动错误
    public void setBlockchainDataDao(BlockchainDataDao blockchainDataDao) {
        this.blockchainDataDao = blockchainDataDao;
    }

    /**
     * 获取图表数据（返回模拟数据）
     */
    public List<Object> getChartData(
            String cryptocurrency, 
            String metric, 
            String analysisType
    ) {
        // 返回模拟数据
        return getMockChartData(cryptocurrency, metric, analysisType);
    }

    /**
     * 批量插入数据（不执行任何操作）
     */
    @Transactional
    public void saveBlockchainData(List<BlockchainDataEntity> dataList) {
        // 不执行任何操作
        System.out.println("模拟保存 " + dataList.size() + " 条数据");
    }
    
    /**
     * 生成模拟数据
     */
    private List<Object> getMockChartData(String cryptocurrency, String metric, String analysisType) {
        List<Object> mockData = new ArrayList<>();
        
        // 生成一些随机数据点
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