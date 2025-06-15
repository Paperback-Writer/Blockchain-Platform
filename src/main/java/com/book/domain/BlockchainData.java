package com.book.domain;

public class BlockchainData {
    private String cryptocurrency;
    private String metric;
    private String imagePath;
    private String additionalInfo;

    // 无参构造函数
    public BlockchainData() {}

    // 带参数构造函数
    public BlockchainData(String cryptocurrency, String metric, String imagePath) {
        this.cryptocurrency = cryptocurrency;
        this.metric = metric;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public String getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(String cryptocurrency) {
        this.cryptocurrency = cryptocurrency;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}