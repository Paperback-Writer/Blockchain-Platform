package com.book.web;

import com.book.domain.Image;
import com.book.service.BlockchainService;
import com.book.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图表管理控制器，用于处理AJAX请求获取图表信息
 */
@Controller
public class ChartImageController {
    
    private final BlockchainService blockchainService;
    private final MetricService metricService;
    
    @Autowired
    public ChartImageController(BlockchainService blockchainService, MetricService metricService) {
        this.blockchainService = blockchainService;
        this.metricService = metricService;
    }
    
    /**
     * 获取指标可用的分析类型
     */
    @RequestMapping(value = "/api/metric-analysis-types", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMetricAnalysisTypes(@RequestParam String metric) {
        Map<String, Object> result = new HashMap<>();
        Set<String> analysisTypes = metricService.getAvailableAnalysisTypes(metric);
        
        result.put("metric", metric);
        result.put("analysisTypes", analysisTypes);
        result.put("description", metricService.getMetricDescription(metric));
        
        return result;
    }
    
    /**
     * 获取图表图片
     */
    @RequestMapping(value = "/api/chart-images", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getChartImages(
            @RequestParam String cryptocurrency,
            @RequestParam String metric,
            @RequestParam String analysisType) {
        
        Map<String, Object> result = new HashMap<>();
        List<Image> images = blockchainService.getBlockchainImages(cryptocurrency, metric, analysisType);
        
        result.put("cryptocurrency", cryptocurrency);
        result.put("metric", metric);
        result.put("analysisType", analysisType);
        result.put("images", images);
        
        return result;
    }
}