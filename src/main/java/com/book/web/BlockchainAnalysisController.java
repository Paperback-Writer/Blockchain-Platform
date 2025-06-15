package com.book.web;

import com.book.domain.Image;
import com.book.service.BlockchainDataService;
import com.book.service.BlockchainService;
import com.book.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class BlockchainAnalysisController {
    
    private final BlockchainService blockchainService;
    private final MetricService metricService;
    private final BlockchainDataService blockchainDataService; // 保留依赖，但不使用
    
    @Autowired
    public BlockchainAnalysisController(
            BlockchainDataService blockchainDataService,
            BlockchainService blockchainService,
            MetricService metricService) {
        this.blockchainDataService = blockchainDataService;
        this.blockchainService = blockchainService;
        this.metricService = metricService;
    }
    
    @RequestMapping(value = "/blockchain.html", method = RequestMethod.GET)
    public ModelAndView showBlockchainAnalysis(
            @RequestParam(value = "cryptocurrency", required = false, defaultValue = "bitcoin") String cryptocurrency,
            @RequestParam(value = "metric", required = false, defaultValue = "Block Size") String metric,
            @RequestParam(value = "analysisType", required = false, defaultValue = "static") String analysisType) {
        
        ModelAndView modelAndView = new ModelAndView("blockchain-analysis");
        
        // 可用的加密货币列表
        String[] cryptocurrencies = {
            "bitcoin", "dogecoin", "bitcash", 
            "monacoin", "feathercoin", "litecoin"
        };
        
        // 根据分析类型选择不同的指标列表
        String[] metrics;
        if ("cluster".equals(analysisType)) {
            // Cluster Analysis模式下的指标列表
            metrics = new String[] {
                "Utxo Active Rate",
                "Daily Coin Destroyed",
                "Cross-chain Economic Feature Comparison",
                "Gini Coefficient",
                "Micro Velocity",
                "Top 1 Ratio",
                "Whale Ratio"
            };
            
            // 如果当前选择的指标不在新的列表中，则设置为默认指标
            if (!Arrays.asList(metrics).contains(metric)) {
                metric = metrics[0]; // 默认选择第一个指标
            }
        } else {
            // 静态分析模式下的指标列表
            metrics = new String[] {
                "Block Size", 
                "Transaction Fees", 
                "Fund Flow", 
                "Gini Index(Empty)", 
                "Micro Velocity", 
                "Inflation", 
                "Rewards", 
                "Transaction Throughput"
            };
            
            // 验证和设置默认值
            if (!Arrays.asList(metrics).contains(metric)) {
                metric = metrics[0];
            }
        }
        
        // 验证加密货币
        if (!Arrays.asList(cryptocurrencies).contains(cryptocurrency)) {
            cryptocurrency = "bitcoin";
        }
        
        // 获取指标对应的可用分析类型
        Set<String> baseTypes = metricService.getAvailableAnalysisTypes(metric);
        // 创建一个新的可修改集合
        Set<String> availableAnalysisTypes = new HashSet<>(baseTypes);
        // 添加cluster分析类型
        availableAnalysisTypes.add("cluster");
        
        // 验证分析类型是否可用，如果不可用则使用第一个可用的分析类型
        if (!availableAnalysisTypes.contains(analysisType)) {
            analysisType = availableAnalysisTypes.isEmpty() ? "static" : availableAnalysisTypes.iterator().next();
        }
        
        // 获取图表描述
        String metricDescription = metricService.getMetricDescription(metric);
        
        // 获取图表图片 - 对于cluster模式，使用特殊的方法
        List<Image> images;
        if ("cluster".equals(analysisType)) {
            // 使用特殊的方法获取cluster模式的图表
            images = blockchainService.getClusterImages(metric);
        } else {
            // 使用原有方法获取特定加密货币的图表
            images = blockchainService.getBlockchainImages(cryptocurrency, metric, analysisType);
        }
        
        // 设置模型属性
        modelAndView.addObject("cryptocurrencies", cryptocurrencies);
        modelAndView.addObject("metrics", metrics); // 传递根据分析类型确定的指标列表
        modelAndView.addObject("selectedCryptocurrency", cryptocurrency);
        modelAndView.addObject("selectedMetric", metric);
        modelAndView.addObject("selectedAnalysisType", analysisType);
        modelAndView.addObject("isClusterMode", "cluster".equals(analysisType)); // 添加一个标志表示是否处于cluster模式
        modelAndView.addObject("metricDescription", metricDescription);
        modelAndView.addObject("images", images);
        
        // 使用空数据代替数据库数据
        try {
            // 尝试获取数据，但捕获任何异常
            List<Object> chartData = blockchainDataService.getChartData(
                cryptocurrency, metric, analysisType
            );
            modelAndView.addObject("chartData", chartData);
        } catch (Exception e) {
            // 发生异常时使用空列表
            System.out.println("无法从数据库获取数据: " + e.getMessage());
            modelAndView.addObject("chartData", new ArrayList<>());
        }
        
        return modelAndView;
    }

    // 可选：添加数据导入的方法
    @RequestMapping(value = "/import-blockchain-data", method = RequestMethod.GET)
    public String importBlockchainData() {
        // 这里可以实现数据导入逻辑
        // 例如从CSV文件或其他数据源批量导入数据到数据库
        return "import-success";
    }
}