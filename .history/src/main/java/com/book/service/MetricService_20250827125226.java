package com.book.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Blockchain metric service class, used to manage metadata and available analysis types for all metrics
 */
@Service
public class MetricService {
    
    // Metric mapping cache
    private final Map<String, Set<String>> metricAnalysisTypes = new HashMap<>();
    private final Map<String, String> metricDescriptions = new HashMap<>();
    private final Map<String, Map<String, List<ChartInfo>>> chartInfoMap = new HashMap<>();
    
    /**
     * Chart information class
     */
    public static class ChartInfo {
        private String title;
        private String path;
        private String description;
        
        public ChartInfo(String title, String path, String description) {
            this.title = title;
            this.path = path;
            this.description = description;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getPath() {
            return path;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Initialize all metric mappings
     */
    @PostConstruct
    public void initializeMetricMappings() {
        // Initialize available analysis types for metrics
        initializeAnalysisTypes();
        
        // Initialize metric descriptions
        initializeMetricDescriptions();
        
        // Initialize chart information
        initializeChartInfo();
    }
    
    /**
     * Initialize available analysis types for metrics
     */
    private void initializeAnalysisTypes() {
        metricAnalysisTypes.put("Block Size", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Transaction Fees", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Fund Flow", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Gini Index", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Micro Velocity", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Inflation", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Rewards", new HashSet<>(Arrays.asList("static")));
        metricAnalysisTypes.put("Transaction Throughput", new HashSet<>(Arrays.asList("static")));
        //metricAnalysisTypes.put("Market Cap and P/E Ratio", new HashSet<>(Arrays.asList("static")));
    }
    /**
     * Initialize metric descriptions
     */
    private void initializeMetricDescriptions() {
        metricDescriptions.put("Block Size", "Heatmap and Scatterplots illustrate the evolution of block sizes over time in the blockchain.");
        metricDescriptions.put("Transaction Fees", "Analysis of the correlation between transaction fees and amounts helps understand user transaction behavior and network congestion. Timeseries shows how the average number of input and output addresses per transaction has changed from blockchain's inception through the year.");
        metricDescriptions.put("Fund Flow", "Fund flow analysis shows the direction and scale of fund movements in the blockchain network, helping identify major holders and transaction patterns.");
        metricDescriptions.put("Gini Index", "The Gini coefficient measures the degree of wealth distribution inequality, analyzing the concentration and distribution of cryptocurrency holdings.");
        metricDescriptions.put("Micro Velocity", "Micro velocity analyzes cryptocurrency circulation speed in short time frames, reflecting market activity and user transaction behavior. Timeseries tracks the spending behavior of the blockchain's UTXOs. And piechart shows how long coins remained unspent, grouped into different holding period categories");
        metricDescriptions.put("Inflation", "Analysis of cryptocurrency issuance mechanisms and inflation rates, comparing economic models of different coins.");
        metricDescriptions.put("Rewards", "Trade-off analysis compares the balance points between security, decentralization, and performance across different blockchains.");
        metricDescriptions.put("Transaction Throughput", "Transaction throughput reflects the ability and efficiency of blockchain networks to process transactions, a key performance indicator. Timeseries illustrates the transactional throughput");
        //metricDescriptions.put("Market Cap and P/E Ratio", "Analysis of cryptocurrency market valuation and value assessment metrics, comparing market performance and investment potential of different coins.");
    }
    
    /**
     * Initialize chart information
     */
    private void initializeChartInfo() {
        // Block Size
        addChartInfo("Block Size", "static", Arrays.asList(
            new ChartInfo("Block Size Heatmap", "Heatmap.png",
                "Shows the distribution of block sizes across different time periods, with red areas indicating larger block sizes."),
            new ChartInfo("Block Size ScatterPlot", "Scatterplot.png",
                "Shows statistical distribution characteristics of block sizes, including median, quartile ranges, and outliers.")
        ));

        // Transaction Fees
        addChartInfo("Transaction Fees", "static", Arrays.asList(
            new ChartInfo("Transaction Fee and Amount Correlation Bar Chart", "Barchart.png",
                "Shows average transaction fees for different amount ranges."),
            new ChartInfo("Transaction Fee Distribution Pie Chart", "Piechart.png",
                "Shows the proportion of transactions at different fee levels."),
            new ChartInfo("Transaction Fee and Amount Scatter Plot", "Scatterplot2.png",
                "Shows the relationship between fees and amounts for each transaction, reflecting correlation strength.")
        ));

        // Fund Flow
        addChartInfo("Fund Flow", "static", Arrays.asList(
            new ChartInfo("Fund Flow Proportion Chart", "fund_flow_ratio.png",
                "Shows the proportion of fund flows between different types of addresses."),
            new ChartInfo("Fund Flow Network Graph", "fund_flow_network.png",
                "Visualizes the fund flow network and relationship strength between major addresses.")
        ));

        // Gini Index
        addChartInfo("Gini Index", "static", Arrays.asList(
            new ChartInfo("Wealth Distribution Heatmap", "gini_heatmap.png",
                "Shows the distribution of addresses across different amount ranges."),
            new ChartInfo("Address Balance Distribution Bar Chart", "gini_bar.png",
                "Shows address counts by balance level, displaying wealth concentration.")
        ));

        // Micro Velocity
        addChartInfo("Micro Velocity", "static", Arrays.asList(
            new ChartInfo("Micro Velocity Bar Chart", "Barchart.png",
                "Shows micro transaction velocity statistics within different time windows.")
        ));

        // Inflation
        addChartInfo("Inflation", "static", Arrays.asList(
            new ChartInfo("Inflation Rate Time Series", "Timeseries.png",
                "Shows the trend of inflation rate changes over time.")
        ));

        // Rewards (Trade-offs)
        addChartInfo("Rewards", "static", Arrays.asList(
            new ChartInfo("Rewards over time", "Rewards.png",
                "Show mining rewards over time.")
        ));

        // Transaction Throughput
        addChartInfo("Throughput", "static", Arrays.asList(
            new ChartInfo("Transaction Throughput Gauge Chart", "Timeseries.png",
                "Uses a gauge chart to show the comparison between current network transaction processing capacity and design limits.")
        ));
    }

    
    /**
     * Add chart information
     */
    private void addChartInfo(String metric, String analysisType, List<ChartInfo> chartInfos) {
        if (!chartInfoMap.containsKey(metric)) {
            chartInfoMap.put(metric, new HashMap<>());
        }
        chartInfoMap.get(metric).put(analysisType, chartInfos);
    }
    
    /**
     * Get available analysis types for a metric
     * @param metric metric name
     * @return set of available analysis types
     */
    public Set<String> getAvailableAnalysisTypes(String metric) {
        return metricAnalysisTypes.getOrDefault(metric, Collections.emptySet());
    }
    
    /**
     * Get metric description
     * @param metric metric name
     * @return metric description
     */
    public String getMetricDescription(String metric) {
        return metricDescriptions.getOrDefault(metric, "");
    }
    
    /**
     * Get chart information for a metric
     * @param metric metric name
     * @param analysisType analysis type
     * @return list of chart information
     */
    public List<ChartInfo> getChartInfo(String metric, String analysisType) {
        Map<String, List<ChartInfo>> analysisTypeMap = chartInfoMap.getOrDefault(metric, Collections.emptyMap());
        return analysisTypeMap.getOrDefault(analysisType, Collections.emptyList());
    }
}