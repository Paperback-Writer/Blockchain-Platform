package com.book.domain;

public class BlockchainDataEntity {
    private Long id;
    private String cryptocurrency;
    private String metric;
    private String analysisType;
    private Double xValue;
    private Double yValue;
    private String additionalInfo;

    // 无参构造函数
    public BlockchainDataEntity() {}

    // 全参构造函数
    public BlockchainDataEntity(Long id, String cryptocurrency, String metric, 
                                String analysisType, Double xValue, Double yValue, 
                                String additionalInfo) {
        this.id = id;
        this.cryptocurrency = cryptocurrency;
        this.metric = metric;
        this.analysisType = analysisType;
        this.xValue = xValue;
        this.yValue = yValue;
        this.additionalInfo = additionalInfo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public Double getxValue() {
        return xValue;
    }

    public void setxValue(Double xValue) {
        this.xValue = xValue;
    }

    public Double getyValue() {
        return yValue;
    }

    public void setyValue(Double yValue) {
        this.yValue = yValue;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}