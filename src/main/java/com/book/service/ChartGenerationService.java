package com.book.service;

import com.book.domain.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

@Service
public class ChartGenerationService {

    @Autowired
    private SSHService sshService;
    
    // 远程服务器信息
    private static final String REMOTE_HOST = "abacus-2.ifi.uzh.ch";
    private static final String REMOTE_USER = "zhongxingdu";
    private static final String REMOTE_PASSWORD = "my99jsyDu@";
    
    // 项目根目录
    private static final String PROJECT_BASE_PATH = System.getProperty("user.dir");
    
    // 本地图片存储路径
    private static final String LOCAL_IMAGE_BASE_PATH = PROJECT_BASE_PATH + "/src/main/webapp/static/blockchain-images/";
    
    // // 脚本映射表 - 存储不同加密货币、指标对应的脚本路径
    // private Map<String, Map<String, String>> scriptPaths = new HashMap<>();
    
    // @PostConstruct
    // public void init() {
    //     initializeScriptPaths();
    // }
    
    // /**
    //  * 初始化脚本路径映射
    //  */
    // private void initializeScriptPaths() {
    //     // 比特币脚本映射
    //     Map<String, String> bitcoinScripts = new HashMap<>();
    //     bitcoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/bitcoin/bitcoin_data/py/Block_Size/Block_Size.py");
    //     // 添加更多比特币指标的脚本路径...
        
    //     // 莱特币脚本映射
    //     Map<String, String> litecoinScripts = new HashMap<>();
    //     litecoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/litecoin/litecoin_data/py/Block_Size/Block_Size.py");
    //     // 添加更多莱特币指标的脚本路径...
        
    //     // 将映射添加到总表中
    //     scriptPaths.put("bitcoin", bitcoinScripts);
    //     scriptPaths.put("litecoin", litecoinScripts);
        
    //     // 为其他加密货币添加映射...
    //     Map<String, String> dogecoinScripts = new HashMap<>();
    //     dogecoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/dogecoin/dogecoin_data/py/Block_Size/Block_Size.py");
    //     scriptPaths.put("dogecoin", dogecoinScripts);
        
    //     Map<String, String> bitcashScripts = new HashMap<>();
    //     bitcashScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/bitcash/bitcash_data/py/Block_Size/Block_Size.py");
    //     scriptPaths.put("bitcash", bitcashScripts);
        
    //     Map<String, String> monacoinScripts = new HashMap<>();
    //     monacoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/monacoin/monacoin_data/py/Block_Size/Block_Size.py");
    //     scriptPaths.put("monacoin", monacoinScripts);
        
    //     Map<String, String> feathercoinScripts = new HashMap<>();
    //     feathercoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/feathercoin/feathercoin_data/py/Block_Size/Block_Size.py");
    //     scriptPaths.put("feathercoin", feathercoinScripts);
    // }
    
    private String getScriptPath(String cryptocurrency, String metric) {
        String folderName = metric.replace(" ", "_");
        String scriptPath = "/local/scratch/master_project_utxo_2025/data/" +
            cryptocurrency + "/" + cryptocurrency + "_data/py/" +
            folderName + "/" + folderName + ".py";

        // 加入调试输出
        System.out.println("[DEBUG] getScriptPath():");
        System.out.println("         cryptocurrency = " + cryptocurrency);
        System.out.println("         metric         = " + metric);
        System.out.println("         folderName     = " + folderName);
        System.out.println("         scriptPath     = " + scriptPath);

        return scriptPath;  
    }



    /**
     * 生成图表
     * @param cryptocurrency 加密货币
     * @param metric 指标
     * @param analysisType 分析类型
     * @return 生成的图像列表
     */
    public List<Image> generateCharts(String cryptocurrency, String metric, String analysisType) {
        // System.out.println("------- generateCharts 调试信息 -------");
        // System.out.println("当前加密货币: " + cryptocurrency);
        // System.out.println("当前指标: " + metric);
        // System.out.println("当前分析类型: " + analysisType);
        List<Image> images = new ArrayList<>();
        
        try {
            // 获取脚本路径
            String scriptPath = getScriptPath(cryptocurrency, metric);

            if (scriptPath == null) {
                System.out.println("未找到对应的脚本路径: " + cryptocurrency + ", " + metric);
                
                // 推测远程目录路径（假设规则一致）
                String baseDir = "/local/scratch/master_project_utxo_2025/data/"
                            + cryptocurrency + "/" + cryptocurrency + "_data/py/"
                            + metric.replace(" ", "_") + "/";
                
                // 建立 SSH 连接
                sshService.connect(REMOTE_HOST, REMOTE_USER, REMOTE_PASSWORD);
                
                // 使用 ls 列出目录下的 png 文件
                String lsCommand = "ls " + baseDir + "*.png";
                String result = sshService.executeCommand(lsCommand);
                System.out.println("获取图片列表: " + result);

                String[] imageNames = result.split("\n");
                
                for (String remoteImagePath : imageNames) {
                    // 获取文件名
                    String imageFileName = remoteImagePath.substring(remoteImagePath.lastIndexOf('/') + 1);
                    
                    // 本地路径
                    String localDirPath = LOCAL_IMAGE_BASE_PATH + cryptocurrency + "/" + metric.replace(" ", "_") + "/" + analysisType + "/";
                    new File(localDirPath).mkdirs();
                    String localFilePath = localDirPath + imageFileName;
                    
                    // 下载图像
                    sshService.downloadFile(remoteImagePath, localFilePath);
                    
                    // 构建前端路径
                    String webPath = "/static/blockchain-images/" + cryptocurrency + "/" + metric.replace(" ", "_") + "/" + analysisType + "/" + imageFileName;
                    
                    // 构建图像对象
                    String title = cryptocurrency + " " + metric + " - Unknown Analysis";
                    String description = "This image was loaded as fallback since no script was found.";
                    
                    images.add(new Image(imageFileName, webPath, title, description));
                }
                
                sshService.disconnect();
                return images;
            }

            
            // 连接到远程服务器
            sshService.connect(REMOTE_HOST, REMOTE_USER, REMOTE_PASSWORD);
            
            // 构建命令 - 添加分析类型参数
            String command = "python " + scriptPath + " --type=" + analysisType;
            
            // 执行命令
            String result = sshService.executeCommand(command);
            System.out.println("命令执行结果: " + result);
            
            // 获取远程图像路径
            String remoteImagePath = getRemoteImagePath(scriptPath);
            
            // 确保目标目录存在
            String localDirPath = LOCAL_IMAGE_BASE_PATH + cryptocurrency + "/" + metric.replace(" ", "_") + "/" + analysisType + "/";
            new File(localDirPath).mkdirs();
            
            // 构建本地文件路径和名称
            String imageFileName = cryptocurrency + "_" + metric.replace(" ", "_").toLowerCase() + "_" + 
                                  analysisType + ".png";
            String localFilePath = localDirPath + imageFileName;
            
            // 下载图像
            sshService.downloadFile(remoteImagePath, localFilePath);
            
            // 构建相对路径（用于前端访问）
            String webPath = "/static/blockchain-images/" + cryptocurrency + "/" + 
                          metric.replace(" ", "_") + "/" + analysisType + "/" + imageFileName;
            
            // 创建图像对象
            String title = cryptocurrency.substring(0, 1).toUpperCase() + cryptocurrency.substring(1) + 
                           " " + metric + " - " + 
                           analysisType.substring(0, 1).toUpperCase() + analysisType.substring(1) + " Analysis";
                           
            String description = "This chart shows " + analysisType + " analysis of " + metric + " for " + cryptocurrency + ".";
            
            Image image = new Image(imageFileName, webPath, title, description);
            images.add(image);
            
        } catch (Exception e) {
            System.err.println("生成图表时出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                sshService.disconnect();
            } catch (Exception e) {
                System.err.println("断开SSH连接时出错: " + e.getMessage());
            }
        }
        
        return images;
    }
    
    /**
     * 获取远程图像路径
     * 假设图像与脚本在同一目录下，且图像文件名与脚本文件名相同（不包括扩展名）
     */
    private String getRemoteImagePath(String scriptPath) {
        int lastSlash = scriptPath.lastIndexOf('/');
        String directory = scriptPath.substring(0, lastSlash + 1);
        String scriptName = scriptPath.substring(lastSlash + 1);
        String imageName = scriptName.substring(0, scriptName.lastIndexOf('.')) + ".png";
        return directory + imageName;

    }
}