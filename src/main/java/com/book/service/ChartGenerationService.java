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
    
    // Remote Server login
    private static final String REMOTE_HOST = "abacus-2.ifi.uzh.ch";
    private static final String REMOTE_USER = "zhongxingdu";
    private static final String REMOTE_PASSWORD = "my99jsyDu@";
    

    private static final String PROJECT_BASE_PATH = System.getProperty("user.dir");
    
    private static final String LOCAL_IMAGE_BASE_PATH = PROJECT_BASE_PATH + "/src/main/webapp/static/blockchain-images/";
    
    // private Map<String, Map<String, String>> scriptPaths = new HashMap<>();
    
    // @PostConstruct
    // public void init() {
    //     initializeScriptPaths();
    // }
    
    // private void initializeScriptPaths() {
    //     Map<String, String> bitcoinScripts = new HashMap<>();
    //     bitcoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/bitcoin/bitcoin_data/py/Block_Size/Block_Size.py");
        
    //     Map<String, String> litecoinScripts = new HashMap<>();
    //     litecoinScripts.put("Block Size", "/local/scratch/master_project_utxo_2025/data/litecoin/litecoin_data/py/Block_Size/Block_Size.py");
        
    //     scriptPaths.put("bitcoin", bitcoinScripts);
    //     scriptPaths.put("litecoin", litecoinScripts);
        
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

        // debug
        System.out.println("[DEBUG] getScriptPath():");
        System.out.println("         cryptocurrency = " + cryptocurrency);
        System.out.println("         metric         = " + metric);
        System.out.println("         folderName     = " + folderName);
        System.out.println("         scriptPath     = " + scriptPath);

        return scriptPath;  
    }



    /**
     * Grph generate
     * @param cryptocurrency 
     * @param metric 
     * @param analysisType
     * @return
     */
    public List<Image> generateCharts(String cryptocurrency, String metric, String analysisType) {

        List<Image> images = new ArrayList<>();
        
        try {
            // get path
            String scriptPath = getScriptPath(cryptocurrency, metric);

            if (scriptPath == null) {
                System.out.println("Script not found:" + cryptocurrency + ", " + metric);
                
                String baseDir = "/local/scratch/master_project_utxo_2025/data/"
                            + cryptocurrency + "/" + cryptocurrency + "_data/py/"
                            + metric.replace(" ", "_") + "/";
                
                // establish ssh
                sshService.connect(REMOTE_HOST, REMOTE_USER, REMOTE_PASSWORD);
                
                String lsCommand = "ls " + baseDir + "*.png";
                String result = sshService.executeCommand(lsCommand);
                System.out.println("Get Chart List:" + result);

                String[] imageNames = result.split("\n");
                
                for (String remoteImagePath : imageNames) {
                    String imageFileName = remoteImagePath.substring(remoteImagePath.lastIndexOf('/') + 1);
                    
                    String localDirPath = LOCAL_IMAGE_BASE_PATH + cryptocurrency + "/" + metric.replace(" ", "_") + "/" + analysisType + "/";
                    new File(localDirPath).mkdirs();
                    String localFilePath = localDirPath + imageFileName;
                    
                    sshService.downloadFile(remoteImagePath, localFilePath);
                    
                    String webPath = "/static/blockchain-images/" + cryptocurrency + "/" + metric.replace(" ", "_") + "/" + analysisType + "/" + imageFileName;
                    
                    String title = cryptocurrency + " " + metric + " - Unknown Analysis";
                    String description = "This image was loaded as fallback since no script was found.";
                    
                    images.add(new Image(imageFileName, webPath, title, description));
                }
                
                sshService.disconnect();
                return images;
            }

            
            sshService.connect(REMOTE_HOST, REMOTE_USER, REMOTE_PASSWORD);
            
            String command = "python " + scriptPath + " --type=" + analysisType;
            
            String result = sshService.executeCommand(command);
            System.out.println("Command Result: " + result);
            
            String remoteImagePath = getRemoteImagePath(scriptPath);
            
            String localDirPath = LOCAL_IMAGE_BASE_PATH + cryptocurrency + "/" + metric.replace(" ", "_") + "/" + analysisType + "/";
            new File(localDirPath).mkdirs();
            
            String imageFileName = cryptocurrency + "_" + metric.replace(" ", "_").toLowerCase() + "_" + 
                                  analysisType + ".png";
            String localFilePath = localDirPath + imageFileName;
            
            sshService.downloadFile(remoteImagePath, localFilePath);
            
            String webPath = "/static/blockchain-images/" + cryptocurrency + "/" + 
                          metric.replace(" ", "_") + "/" + analysisType + "/" + imageFileName;
            
            String title = cryptocurrency.substring(0, 1).toUpperCase() + cryptocurrency.substring(1) + 
                           " " + metric + " - " + 
                           analysisType.substring(0, 1).toUpperCase() + analysisType.substring(1) + " Analysis";
                           
            String description = "This chart shows " + analysisType + " analysis of " + metric + " for " + cryptocurrency + ".";
            
            Image image = new Image(imageFileName, webPath, title, description);
            images.add(image);
            
        } catch (Exception e) {
            System.err.println("Error generating graph " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                sshService.disconnect();
            } catch (Exception e) {
                System.err.println("error stop ssh" + e.getMessage());
            }
        }
        
        return images;
    }
    
    private String getRemoteImagePath(String scriptPath) {
        int lastSlash = scriptPath.lastIndexOf('/');
        String directory = scriptPath.substring(0, lastSlash + 1);
        String scriptName = scriptPath.substring(lastSlash + 1);
        String imageName = scriptName.substring(0, scriptName.lastIndexOf('.')) + ".png";
        return directory + imageName;

    }
}