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
     * 获取区块链图片
     * @param cryptocurrency 加密货币
     * @param metric 指标
     * @param analysisType 分析类型
     * @return 图片列表
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
    // 为Cluster Analysis模式添加特殊的指标文件夹映射
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
        String basePath; // 用于拼接“对外可访问”的 URL

        if ("cluster".equals(folderAnalysisType)) {
            metricFolder = CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
            // 外部 Context：/blockchain-images/cluster/<abbr>/
            basePath = "/blockchain-images/cluster/" + metricFolder + "/";
        } else {
            metricFolder = METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
            // 外部 Context：/blockchain-images/<coin>/<metricFolder>/<analysisType>/
            basePath = "/blockchain-images/" + cryptocurrency + "/" + metricFolder + "/" + folderAnalysisType + "/";
        }

        for (ChartInfo chartInfo : chartInfos) {
            String fileName = new File(chartInfo.getPath()).getName(); // 仅取文件名
            images.add(new Image(chartInfo.getTitle(), basePath + fileName));
        }

        System.out.println("最终返回的图片数量: " + images.size());
        System.out.println("chartInfos.size=" + chartInfos.size());
        for (int i = 0; i < Math.min(5, chartInfos.size()); i++) {
            System.out.println("chartInfos[" + i + "] title=" + chartInfos.get(i).getTitle()
                + ", path=" + chartInfos.get(i).getPath());
        }
        return images;
    }
    /**
     * 扫描目录查找所有图片文件
     */
    private List<Image> scanDirectoryForImages(String cryptocurrency, String metric, String analysisType) {
        List<Image> images = new ArrayList<>();

        // 获取映射路径信息
        String metricFolder = METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
        String dataFolder = DATA_FOLDER_MAP.getOrDefault(cryptocurrency, cryptocurrency + "_data");
        String symbolFolder = SYMBOL_FOLDER_MAP.getOrDefault(cryptocurrency, cryptocurrency.toUpperCase());

        // 构建远程目录路径 - 根据分析类型决定路径
        String remoteFolder;
        if ("cluster".equals(analysisType)) {
            // 对于 cluster 分析类型，使用不同的路径模式
            remoteFolder = "/local/scratch/master_project_utxo_2025/data/cluster/";
        } else {
            // 对于其他分析类型（如static），使用原有路径模式
            remoteFolder = "/local/scratch/master_project_utxo_2025/data/" +
                cryptocurrency + "/" + symbolFolder + "/" + metricFolder + "/";
        }

        // 构建本地目录路径
        String localFolder = IMAGE_BASE_PATH + cryptocurrency + "/" + metricFolder + "/" + analysisType + "/";
        new File(localFolder).mkdirs();

        try {
            sshService.connect("abacus-2.ifi.uzh.ch", "zhongxingdu", "my99jsyDu@");

            // 1. 扫描远程 png 文件
            String command = "ls " + remoteFolder + "*.png";
            String result = sshService.executeCommand(command);
            System.out.println("远程目录扫描结果:\n" + result);

            String[] imagePaths = result.split("\n");

            for (String remoteImagePath : imagePaths) {
                String fileName = remoteImagePath.substring(remoteImagePath.lastIndexOf('/') + 1);
                String localFilePath = localFolder + fileName;

                // 2. 下载到本地
                sshService.downloadFile(remoteImagePath, localFilePath);

                // 3. 构建 Web 路径
                String webPath = "/static/blockchain-images/" +
                        cryptocurrency + "/" + metricFolder + "/" + analysisType + "/" + fileName;

                String title = getImageTitle(fileName, metric, analysisType);
                images.add(new Image(fileName, webPath, title));
            }

        } catch (Exception e) {
            System.err.println("远程扫描或下载图片失败: " + e.getMessage());
        } finally {
            try {
                sshService.disconnect();
            } catch (Exception e) {
                System.err.println("断开 SSH 连接失败: " + e.getMessage());
            }
        }

        return images;
    }
    public List<Image> getClusterImages(String metric) {
        return new ArrayList<>();
        // List<Image> images = new ArrayList<>();
        
        // // 日志输出
        // System.out.println("------- Cluster模式图片获取调试信息 -------");
        // System.out.println("项目根目录: " + PROJECT_BASE_PATH);
        // System.out.println("图片基础路径: " + IMAGE_BASE_PATH);
        // System.out.println("当前指标: " + metric);
        // System.out.println("分析类型: cluster");
        
        // // 获取映射路径信息 - 使用专门的Cluster指标映射
        // String metricFolder = CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
        // System.out.println("指标文件夹缩写: " + metricFolder);
        
        // // 构建远程目录路径
        // String remoteFolder = "/local/scratch/master_project_utxo_2025/data/cluster/" + metricFolder + "/";
        // System.out.println("使用cluster通用路径: " + remoteFolder);
        
        // // 构建本地目录路径
        // String localFolder = IMAGE_BASE_PATH + "cluster/" + metricFolder + "/";
        // new File(localFolder).mkdirs();
        
        // try {
        //     sshService.connect("abacus-2.ifi.uzh.ch", "zhongxingdu", "my99jsyDu@");
            
        //     // 扫描远程png文件
        //     String command = "ls " + remoteFolder + "*.png";
        //     System.out.println("执行命令: " + command);
        //     String result = sshService.executeCommand(command);
        //     System.out.println("远程目录扫描结果:\n" + result);
            
        //     // 检查结果是否为空
        //     if (result == null || result.trim().isEmpty()) {
        //         System.out.println("远程目录没有找到图片文件");
        //         return images;
        //     }
            
        //     String[] imagePaths = result.split("\n");
            
        //     for (String remoteImagePath : imagePaths) {
        //         String fileName = remoteImagePath.substring(remoteImagePath.lastIndexOf('/') + 1);
        //         String localFilePath = localFolder + fileName;
                
        //         // 下载到本地
        //         System.out.println("下载文件 - 远程路径: " + remoteImagePath + ", 本地路径: " + localFilePath);
        //         sshService.downloadFile(remoteImagePath, localFilePath);
        //         System.out.println("文件下载成功!");
                
        //         // 构建Web路径
        //         String webPath = "/static/blockchain-images/cluster/" + metricFolder + "/" + fileName;
                
        //         // 为图片生成更友好的标题
        //         String title = getClusterImageTitle(fileName, metric);
        //         images.add(new Image(fileName, webPath, title));
        //     }
            
        // } catch (Exception e) {
        //     System.err.println("远程扫描或下载图片失败: " + e.getMessage());
        //     e.printStackTrace();
        // } finally {
        //     try {
        //         sshService.disconnect();
        //         System.out.println("已断开SSH连接");
        //     } catch (Exception e) {
        //         System.err.println("断开SSH连接失败: " + e.getMessage());
        //     }
        // }
        
        // return images;
    }


    /**
     * 将分析类型映射为文件夹名称
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
        // 移除文件扩展名
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        
        // 将下划线替换为空格并格式化
        String formattedName = nameWithoutExtension.replace('_', ' ');
        
        // 首字母大写
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
     * 输出调试信息
     */
    private void logDebugInfo(String cryptocurrency, String metric, String analysisType) {
        System.out.println("------- 区块链图片获取调试信息 -------");
        System.out.println("项目根目录: " + PROJECT_BASE_PATH);
        System.out.println("图片基础路径: " + IMAGE_BASE_PATH);
        System.out.println("当前加密货币: " + cryptocurrency);
        System.out.println("当前指标: " + metric);
        System.out.println("分析类型: " + analysisType);
        System.out.println("文件夹分析类型: " + mapAnalysisTypeToFolder(analysisType));
    }

    private static final String REMOTE_BASE = "/local/scratch/master_project_utxo_2025/graph";

    public void syncImagesFromRemote(String cryptocurrency, String metric, String analysisType) throws Exception {
        
        final String LOCAL_BASE = "/home/kirisamarisa123/Blockchain-Platform/blockchain-images";

        // 映射
        String metricFolder = METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
        String symbol      = SYMBOL_FOLDER_MAP.getOrDefault(cryptocurrency, cryptocurrency.toUpperCase());

        // === 远端目录（按你最新说明走 /graph/...）===
        final String remoteFolder;
        if ("cluster".equalsIgnoreCase(analysisType)) {
            // cluster 模式：/graph/cluster/<缩写>/
            String clusterMetric = CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"));
            remoteFolder = REMOTE_BASE + "/cluster/" + clusterMetric + "/";
        } else {
            // 非 cluster：/graph/<SYMBOL>/<MetricFolder>/
            remoteFolder = REMOTE_BASE + "/" + symbol + "/" + metricFolder + "/";
        }

        // === 本地落盘目录（保留你原先的本地结构，analysisType 只存在本地）===
        File localDir = new File(
            LOCAL_BASE + "/" + (
                "cluster".equalsIgnoreCase(analysisType)
                ? "cluster/" + CLUSTER_METRIC_FOLDER_MAP.getOrDefault(metric, metric.replace(" ", "_"))
                : cryptocurrency + "/" + metricFolder + "/" + analysisType
            )
        );
        if (!localDir.exists()) localDir.mkdirs();

        // 连接并拉取
        sshService.connect("abacus-2.ifi.uzh.ch", "zhongxingdu", "my99jsyDu@");
        try {
            // 列出远端 PNG（目录里直接放图片，没有 static 子目录）
            String cmd = "ls -1 " + remoteFolder + "*.png 2>/dev/null || true";
            String ls  = sshService.executeCommand(cmd);

            if (ls == null || ls.trim().isEmpty()) {
                System.out.println("远端没有找到 PNG 文件: " + remoteFolder);
                return;
            }

            for (String line : ls.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String fileName = line.substring(line.lastIndexOf('/') + 1);
                File localFile  = new File(localDir, fileName);
                sshService.downloadFile(line, localFile.getAbsolutePath()); // 覆盖即可
            }
        } finally {
            sshService.disconnect();
        }
    }


}


