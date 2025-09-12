package com.book.web;

import com.book.domain.Image;
import com.book.service.BlockchainDataService;
import com.book.service.BlockchainService;
import com.book.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class BlockchainAnalysisController {

    private final BlockchainService blockchainService;
    private final MetricService metricService;
    private final BlockchainDataService blockchainDataService;

    @Autowired
    public BlockchainAnalysisController(
            BlockchainDataService blockchainDataService,
            BlockchainService blockchainService,
            MetricService metricService) {
        this.blockchainDataService = blockchainDataService;
        this.blockchainService = blockchainService;
        this.metricService = metricService;
    }

    private void rewriteImagePathsToWebUrl(List<Image> images) {
        if (images == null) return;

        final String fsRoot = "/home/kirisamarisa123/Blockchain-Platform/target/blockchain/static/blockchain-images/";
        final String urlRoot = "/blockchain-images/";

        for (Image img : images) {
            if (img == null) continue;
            String p = img.getPath();
            if (p == null || p.isEmpty()) continue;

            String lower = p.toLowerCase(Locale.ROOT);
            if (lower.startsWith("http://") || lower.startsWith("https://") || p.startsWith(urlRoot)) {
                continue;
            }
            p = p.replace('\\', '/');
            if (p.startsWith(fsRoot)) {
                String tail = p.substring(fsRoot.length());
                while (tail.startsWith("/")) tail = tail.substring(1);
                p = urlRoot + tail;
            }
            img.setPath(p);
        }
    }

    @RequestMapping(value = "/blockchain.html", method = RequestMethod.GET)
    public ModelAndView showBlockchainAnalysis(
            @RequestParam(value = "cryptocurrency", required = false, defaultValue = "bitcoin") String cryptocurrency,
            @RequestParam(value = "metric", required = false, defaultValue = "Block Size") String metric,
            @RequestParam(value = "analysisType", required = false, defaultValue = "static") String analysisType) {

        ModelAndView modelAndView = new ModelAndView("blockchain-analysis");

        String[] cryptocurrencies = {"bitcoin", "dogecoin", "bitcash", "monacoin", "feathercoin", "litecoin"};

        String[] metrics;
        if ("cluster".equals(analysisType)) {
            metrics = new String[]{
                    "Utxo Active Rate",
                    "Balance VS Mining Concentration",
                    "Cross-chain Economic Feature Comparison",
                    "Gini Coefficient",
                    "Mining Reward Concentration",
                    "Top 1 Ratio",
                    "Whale Ratio"
            };
            if (!Arrays.asList(metrics).contains(metric)) {
                metric = metrics[0];
            }
        } else {
            metrics = new String[]{
                    "Block Size",
                    "Transaction Fees",
                    "Fund Flow",
                    "Gini Index",
                    "Micro Velocity",
                    "Inflation",
                    "Rewards",
                    "Transaction Throughput"
            };
            if (!Arrays.asList(metrics).contains(metric)) {
                metric = metrics[0];
            }
        }

        if (!Arrays.asList(cryptocurrencies).contains(cryptocurrency)) {
            cryptocurrency = "bitcoin";
        }

        Set<String> baseTypes = metricService.getAvailableAnalysisTypes(metric);
        Set<String> availableAnalysisTypes = new HashSet<>(baseTypes);
        availableAnalysisTypes.add("cluster");
        if (!availableAnalysisTypes.contains(analysisType)) {
            analysisType = availableAnalysisTypes.isEmpty() ? "static" : availableAnalysisTypes.iterator().next();
        }

        String metricDescription = metricService.getMetricDescription(metric);

        List<Image> images = blockchainService.getBlockchainImages(cryptocurrency, metric, analysisType);
        rewriteImagePathsToWebUrl(images);

        modelAndView.addObject("cryptocurrencies", cryptocurrencies);
        modelAndView.addObject("metrics", metrics);
        modelAndView.addObject("selectedCryptocurrency", cryptocurrency);
        modelAndView.addObject("selectedMetric", metric);
        modelAndView.addObject("selectedAnalysisType", analysisType);
        modelAndView.addObject("isClusterMode", "cluster".equals(analysisType));
        modelAndView.addObject("metricDescription", metricDescription);
        modelAndView.addObject("images", images);

        try {
            List<Object> chartData = blockchainDataService.getChartData(cryptocurrency, metric, analysisType);
            modelAndView.addObject("chartData", chartData);
        } catch (Exception e) {
            System.out.println("无法从数据库获取数据: " + e.getMessage());
            modelAndView.addObject("chartData", new ArrayList<>());
        }

        return modelAndView;
    }

    @RequestMapping(value = "/update-images", method = RequestMethod.POST)
    public String updateImages(
            @RequestParam("cryptocurrency") String cryptocurrency,
            @RequestParam("metric") String metric,
            @RequestParam("analysisType") String analysisType) {
        try {
            blockchainService.syncImagesFromRemote(cryptocurrency, metric, analysisType);
            return "redirect:/blockchain.html?cryptocurrency=" + cryptocurrency
                    + "&metric=" + urlEncode(metric)
                    + "&analysisType=" + analysisType
                    + "&updated=1";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/blockchain.html?cryptocurrency=" + cryptocurrency
                    + "&metric=" + urlEncode(metric)
                    + "&analysisType=" + analysisType
                    + "&updated=0";
        }
    }

    // 也要放在类内
    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception ex) {
            return s;
        }
    }
}
