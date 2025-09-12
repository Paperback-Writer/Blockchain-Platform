package com.book.service;

import com.book.domain.Image;
import com.book.service.MetricService.ChartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


@Service
public class BlockchainService {
    
    private static final String PROJECT_BASE_PATH = System.getProperty("user.dir");
    private static final String IMAGE_BASE_PATH = PROJECT_BASE_PATH + "/src/main/webapp/static/blockchain-images/";
    
    private final MetricService metricService;
    private final ChartGenerationService chartGenerationService;
    
    @Autowired
    private SSHService sshService;
    @Autowired
    public BlockchainService(MetricService metricService, ChartGenerationService chartGenerationService) {
        this.metricService = metricService;
        this.chartGenerationService = chartGenerationService;
    }
    

    /**
     * Get blockchain images
     * @param cryptocurrency cryptocurrency
     * @param metric metric
     * @param analysisType analysis type
     * @return list of images
     */

    private static final Map<String, String> DATA_FOLDER_MAP = new HashMap<>();
    static {
        DATA_FOLDER_MAP.put("bitcoin", "bitcoin");
        DATA_FOLDER_MAP.put("litecoin", "litecoin");
        DATA_FOLDER_MAP.put("dogecoin", "dogecoin");
        DATA_FOLDER_MAP.put("bitcash", "bcash");
        DATA_FOLDER_MAP.put("monacoin", "monacoin");
        DATA_FOLDER_MAP.put("feathercoin", "feathercoin");
    }

    private static final Map<String, String> SYMBOL_FOLDER_MAP = new HashMap<>();
    static {
        SYMBOL_FOLDER_MAP.put("bitcoin", "BTC");
        SYMBOL_FOLDER_MAP.put("litecoin", "LTC");
        SYMBOL_FOLDER_MAP.put("dogecoin", "DOGE");
        SYMBOL_FOLDER_MAP.put("bitcash", "BCH");
        SYMBOL_FOLDER_MAP.put("monacoin", "MON");
        SYMBOL_FOLDER_MAP.put("feathercoin", "FTC");
    }


    private static final Map<String, String> METRIC_FOLDER_MAP = new HashMap<>();
    static {
        METRIC_FOLDER_MAP.put("Block Size", "Block_Size");
        METRIC_FOLDER_MAP.put("Inflation", "Inflation");
        METRIC_FOLDER_MAP.put("Fund Flow", "Fund_Flow");
        METRIC_FOLDER_MAP.put("Micro Velocity", "Microvelocity");
        METRIC_FOLDER_MAP.put("Rewards", "Trade-offs");
        METRIC_FOLDER_MAP.put("Transaction Throughput", "Throughput");
        METRIC_FOLDER_MAP.put("Transaction Fees", "Transaction_Fee");
        METRIC_FOLDER_MAP.put("Gini Index", "Gini");
    }
    // Special metric folder map for Cluster Analysis mode
    private static final Map<String, String> CLUSTER_METRIC_FOLDER_MAP = new HashMap<>();
    static {
        CLUSTER_METRIC_FOLDER_MAP.put("Utxo Active Rate", "UAR");
        CLUSTER_METRIC_FOLDER_MAP.put("Balance VS Mining Concentration", "BM");
        CLUSTER_METRIC_FOLDER_MAP.put("Cross-chain Economic Feature Comparison", "CEC");
        CLUSTER_METRIC_FOLDER_MAP.put("Gini Coefficient", "GC");
        CLUSTER_METRIC_FOLDER_MAP.put("Mining Reward Concentration", "MRC");
        CLUSTER_METRIC_FOLDER_MAP.put("Top 1 Ratio", "T1R");
        CLUSTER_METRIC_FOLDER_MAP.put("Whale Ratio", "WR");
    }

    public List<Image> getBlockchainImages(String cryptocurrency, String metric, String analysisType) {
        List<Image> images = new ArrayList<>();
        logDebugInfo(cryptocurrency, metric, analysisType);

        List<ChartInfo> chartInfos = metricService.getChartInfo(metric, analysisType);
        String folderAnalysisType = mapAnalysisTypeToFolder(analysisType);

        String metricFolder;
        String basePath; // Used to compose publicly accessible URL

        if ("cluster".equals(folderAnalysisType)) {
            metricFolder = CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
            // External context: /blockchain-images/cluster/<abbr>/
            basePath = "/blockchain-images/cluster/" + metricFolder + "/";
        } else {
            metricFolder = METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
            // External context: /blockchain-images/<coin>/<metricFolder>/<analysisType>/
            basePath = "/blockchain-images/" + cryptocurrency + "/" + metricFolder + "/" + folderAnalysisType + "/";
        }

        for (ChartInfo chartInfo : chartInfos) {
            String fileName = new File(chartInfo.getPath()).getName(); // only take filename
            images.add(new Image(chartInfo.getTitle(), basePath + fileName));
        }

        System.out.println("Final number of images returned: " + images.size());
        System.out.println("chartInfos.size=" + chartInfos.size());
        for (int i = 0; i < Math.min(5, chartInfos.size()); i++) {
            System.out.println("chartInfos[" + i + "] title=" + chartInfos.get(i).getTitle()
                + ", path=" + chartInfos.get(i).getPath());
        }
        return images;
    }
    /**
     * Scan a directory to find all image files
     */
    private List<Image> scanDirectoryForImages(String cryptocurrency, String metric, String analysisType) {
        List<Image> images = new ArrayList<>();

        // Get mapped path info
        String metricFolder = METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
        String dataFolder = DATA_FOLDER_MAP.getOrDefault(cryptocurrency, cryptocurrency + "_data");
        String symbolFolder = SYMBOL_FOLDER_MAP.getOrDefault(cryptocurrency, cryptocurrency.toUpperCase());

        // Build remote directory path - decide by analysis type
        String remoteFolder;
        if ("cluster".equals(analysisType)) {
            // For cluster analysis type, use a different path pattern
            remoteFolder = "/local/scratch/master_project_utxo_2025/data/cluster/";
        } else {
            // For other analysis types (e.g., static), use the original path pattern
            remoteFolder = "/local/scratch/master_project_utxo_2025/data/" +
                cryptocurrency + "/" + symbolFolder + "/" + metricFolder + "/";
        }

        // Build local directory path
        String localFolder = IMAGE_BASE_PATH + cryptocurrency + "/" + metricFolder + "/" + analysisType + "/";
        new File(localFolder).mkdirs();

        try {
            sshService.connect("abacus-2.ifi.uzh.ch", "zhongxingdu", "my99jsyDu@");

            // 1) Scan remote png files
            String command = "ls " + remoteFolder + "*.png";
            String result = sshService.executeCommand(command);
            System.out.println("Remote directory scan result:\n" + result);

            String[] imagePaths = result.split("\n");

            for (String remoteImagePath : imagePaths) {
                String fileName = remoteImagePath.substring(remoteImagePath.lastIndexOf('/') + 1);
                String localFilePath = localFolder + fileName;

                // 2) Download to local
                sshService.downloadFile(remoteImagePath, localFilePath);

                // 3) Build web path
                String webPath = "/static/blockchain-images/" +
                        cryptocurrency + "/" + metricFolder + "/" + analysisType + "/" + fileName;

                String title = getImageTitle(fileName, metric, analysisType);
                images.add(new Image(fileName, webPath, title));
            }

        } catch (Exception e) {
            System.err.println("Failed to scan or download remote images: " + e.getMessage());
        } finally {
            try {
                sshService.disconnect();
            } catch (Exception e) {
                System.err.println("Failed to disconnect SSH: " + e.getMessage());
            }
        }

        return images;
    }

    /**
     * Map analysis type to folder name
     */
    private String mapAnalysisTypeToFolder(String analysisType) {
        switch (analysisType) {
            case "static":
                return "static";
            case "temporal":
                return "temporal";
            case "comparison":
                return "comparison";
            case "cluster":
                return "cluster";
            default:
                return analysisType;
        }
    }
    
    private String getImageTitle(String fileName, String metric, String analysisType) {
        // Remove file extension
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        
        // Replace underscores with spaces and format
        String formattedName = nameWithoutExtension.replace('_', ' ');
        
        // Capitalize first letter
        formattedName = formattedName.substring(0, 1).toUpperCase() + formattedName.substring(1);
        
        return formattedName;
    }
    private String getClusterImageTitle(String fileName, String metric) {
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        String formattedName = nameWithoutExtension.replace('_', ' ');
        formattedName = formattedName.substring(0, 1).toUpperCase() + formattedName.substring(1);
        return metric + " - " + formattedName;
    }
    /**
     * Print debug info
     */
    private void logDebugInfo(String cryptocurrency, String metric, String analysisType) {
        System.out.println("------- Debug info for blockchain image retrieval -------");
        System.out.println("Project base path: " + PROJECT_BASE_PATH);
        System.out.println("Image base path: " + IMAGE_BASE_PATH);
        System.out.println("Cryptocurrency: " + cryptocurrency);
        System.out.println("Metric: " + metric);
        System.out.println("Analysis type: " + analysisType);
        System.out.println("Folder analysis type: " + mapAnalysisTypeToFolder(analysisType));
    }

    private static final String REMOTE_BASE = "/local/scratch/master_project_utxo_2025/graph";

    public void syncImagesFromRemote(String cryptocurrency, String metric, String analysisType) throws Exception {
        
        final String LOCAL_BASE = "/home/kirisamarisa123/Blockchain-Platform/blockchain-images";

        // Mapping
        String metricFolder = METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
        String symbol      = SYMBOL_FOLDER_MAP.getOrDefault(cryptocurrency, cryptocurrency.toUpperCase());

        // === Remote directory (according to your latest note, under /graph/...) ===
        final String remoteFolder;
        if ("cluster".equalsIgnoreCase(analysisType)) {
            // cluster mode: /graph/cluster/<abbr>/
            String clusterMetric = CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
            remoteFolder = REMOTE_BASE + "/cluster/" + clusterMetric + "/";
        } else {
            // non-cluster: /graph/<SYMBOL>/<MetricFolder>/
            remoteFolder = REMOTE_BASE + "/" + symbol + "/" + metricFolder + "/";
        }

        // === Local output directory (keep your original local structure; analysisType exists locally only) ===
        File localDir = new File(
            LOCAL_BASE + "/" + (
                "cluster".equalsIgnoreCase(analysisType)
                ? "cluster/" + CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"))
                : cryptocurrency + "/" + metricFolder + "/" + analysisType
            )
        );
        if (!localDir.exists()) localDir.mkdirs();

        // Connect and fetch
        sshService.connect("abacus-2.ifi.uzh.ch", "zhongxingdu", "my99jsyDu@");
        try {
            // List remote PNGs (images are directly inside the directory, no static subfolder)
            String cmd = "ls -1 " + remoteFolder + "*.png 2>/dev/null || true";
            String ls  = sshService.executeCommand(cmd);

            if (ls == null || ls.trim().isEmpty()) {
                System.out.println("No PNG found on the remote path: " + remoteFolder);
                return;
            }

            for (String line : ls.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String fileName = line.substring(line.lastIndexOf('/') + 1);
                File localFile  = new File(localDir, fileName);
                sshService.downloadFile(line, localFile.getAbsolutePath()); // overwrite is fine
            }
        } finally {
            sshService.disconnect();
        }
    }

}

