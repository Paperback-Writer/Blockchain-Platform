<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<html>
<head>
    <title>Blockchain Analysis Platform</title>
    <link rel="stylesheet" href="${contextPath}/static/css/bootstrap.min.css">
    <script src="${contextPath}/static/js/jquery-3.2.1.js"></script>
    <script src="${contextPath}/static/js/bootstrap.min.js"></script>
    <style>
        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            background-image: url('${contextPath}/static/images/background.jpg');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            background-attachment: fixed;
        }
        .container {
            background-color: rgba(255, 255, 255, 0.8);
            padding: 20px;
            border-radius: 10px;
            margin-top: 20px;
            flex: 1;
        }
        
        /* Left metrics styles */
        .metrics-list {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding-right: 10px;
            margin-top: 15px; /* Increased margin to align with dropdown */
        }
        
        .metric-tab {
            width: 100%;
            text-align: center;
            padding: 10px 5px;
            margin-bottom: 10px;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-weight: bold;
            color: #495057;
            transition: all 0.3s ease;
            cursor: pointer;
            display: flex;
            justify-content: center;
            align-items: center;
            text-decoration: none;
            height: 60px; /* Fixed exact height for all buttons */
            font-size: 13px; /* Even smaller font size */
            overflow: hidden;
            line-height: 1.2; /* Improved line height for multi-line text */
            word-wrap: break-word; /* Allow word wrapping */
        }
        
        .metric-tab:hover {
            background-color: #e9ecef;
        }
        
        .metric-tab.active {
            background-color: #e9ecef;
            border-color: #adb5bd;
        }

        /* Dropdown menu optimization */
        .custom-dropdown-width {
            width: auto;
            min-width: 200px;
            max-width: 280px;
        }

        /* Side-by-side dropdowns */
        .dropdown-container {
            display: flex;
            align-items: center;
            gap: 15px; /* Space between dropdowns */
            margin-top: 10px; /* Added to align with metric column header */
        }
        
        /* Align section headers */
        .section-header {
            margin-bottom: 10px;
            text-align: center;
            font-size: 18px;
            font-weight: 500;
            height: 27px; /* Fixed height for headers to ensure alignment */
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* Image display area */
        .blockchain-image-container {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 30px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background: white;
            padding: 15px;
        }

        .blockchain-image-container img {
            max-width: 100%;
            height: auto;
            object-fit: contain;
        }
        
        /* Chart description area */
        .chart-description {
            margin-top: 10px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
            border-left: 4px solid #6c757d;
        }
        
        /* Analysis type tags */
        .analysis-type-tags {
            display: flex;
            flex-wrap: wrap;
            margin-bottom: 15px;
        }
        
        .analysis-type-tag {
            padding: 5px 10px;
            margin-right: 10px;
            margin-bottom: 10px;
            border-radius: 20px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            transition: all 0.2s;
        }
        
        .analysis-type-tag.static {
            background-color: #e7f5ff;
            color: #1864ab;
            border: 1px solid #74c0fc;
        }
        
        .analysis-type-tag.temporal {
            background-color: #f3f0ff;
            color: #5f3dc4;
            border: 1px solid #b197fc;
        }
        
        .analysis-type-tag.comparison {
            background-color: #fff9db;
            color: #e67700;
            border: 1px solid #ffd43b;
        }
        
        .analysis-type-tag.cluster {
            background-color: #e7fff0;
            color: #2b8a3e;
            border: 1px solid #8ce99a;
        }
        
        .analysis-type-tag.active {
            transform: scale(1.05);
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .analysis-type-tag.static.active {
            background-color: #1864ab;
            color: white;
        }
        
        .analysis-type-tag.temporal.active {
            background-color: #5f3dc4;
            color: white;
        }
        
        .analysis-type-tag.comparison.active {
            background-color: #e67700;
            color: white;
        }
        
        .analysis-type-tag.cluster.active {
            background-color: #2b8a3e;
            color: white;
        }
        
        /* Style for when no chart is available */
        .no-chart-available {
            padding: 30px;
            text-align: center;
            background-color: #f8f9fa;
            border-radius: 5px;
            color: #6c757d;
            font-style: italic;
        }
        
        /* Chart container title */
        .chart-container-title {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 10px;
            color: #343a40;
            border-bottom: 2px solid #dee2e6;
            padding-bottom: 5px;
        }
        
        /* Chart info */
        .chart-info {
            font-size: 14px;
            margin-top: 5px;
            color: #6c757d;
        }
        
        footer {
            margin-top: 30px;
            padding: 10px 0;
            background-color: rgba(0, 0, 0, 0.1);
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="text-center my-4">Blockchain Analysis Platform</h1>
        
        <div class="row">
            <!-- Left metrics selection -->
            <div class="col-md-3">
                <div style="margin-top: 8px;"> <!-- Extra spacing to align with dropdown -->
                    <h5 class="section-header">Economic Metrics</h5>
                    <div class="metrics-list">
                        <c:forEach items="${metrics}" var="metric">
                            <!-- 只有当analysisType不是cluster或metric不是Market Cap时才显示 -->
                            <c:if test="${!(selectedAnalysisType == 'cluster' && metric == 'Market Cap')}">
                                <a class="metric-tab ${selectedMetric == metric ? 'active' : ''}" 
                                href="${contextPath}/blockchain.html?cryptocurrency=${selectedCryptocurrency}&metric=${metric}&analysisType=${selectedAnalysisType}">
                                    ${metric}
                                </a>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <!-- Right content area -->
            <div class="col-md-9">
                <!-- 使用GET方法直接提交，不使用JavaScript -->
                <form id="analysisForm" method="get" action="${contextPath}/blockchain.html">
                    <div class="dropdown-container">
                        <!-- Analysis Type Selection -->
                        <div>
                            <label for="analysisTypeSelect">Analysis Type:</label>
                            <select class="form-control form-control-sm custom-dropdown-width" 
                                    id="analysisTypeSelect" 
                                    name="analysisType"
                                    onchange="this.form.submit()">
                                <option value="static" ${selectedAnalysisType == 'static' ? 'selected' : ''}>Static Analysis</option>
                                <option value="cluster" ${selectedAnalysisType == 'cluster' ? 'selected' : ''}>Cluster Analysis</option>
                            </select>
                        </div>

                        <!-- Cryptocurrency selection - 只在非cluster模式下显示 -->
                        <c:if test="${selectedAnalysisType != 'cluster'}">
                            <div>
                                <label for="cryptocurrency">Select Cryptocurrency:</label>
                                <select class="form-control form-control-sm custom-dropdown-width" 
                                        id="cryptocurrency" 
                                        name="cryptocurrency" 
                                        onchange="this.form.submit()">
                                    <c:forEach items="${cryptocurrencies}" var="crypto">
                                        <option value="${crypto}" ${selectedCryptocurrency == crypto ? 'selected' : ''}>
                                            <c:choose>
                                                <c:when test="${crypto == 'bitcoin'}">Bitcoin</c:when>
                                                <c:when test="${crypto == 'dogecoin'}">Dogecoin</c:when>
                                                <c:when test="${crypto == 'bitcash'}">Bitcoin Cash</c:when>
                                                <c:when test="${crypto == 'monacoin'}">Monacoin</c:when>
                                                <c:when test="${crypto == 'feathercoin'}">Feathercoin</c:when>
                                                <c:when test="${crypto == 'litecoin'}">Litecoin</c:when>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:if>

                        <!-- 当处于cluster模式时，添加一个解释 -->
                        <c:if test="${selectedAnalysisType == 'cluster'}">
                            <div class="alert alert-info mt-2">
                                <small>Cluster Analysis mode shows statistics across all cryptocurrencies.</small>
                            </div>
                        </c:if>
                    </div>
                </form>

                <!-- Chart description area -->
                <div class="chart-description mb-4">
                    <h5>${selectedMetric} - ${selectedAnalysisType == 'static' ? 'Static Analysis' : (selectedAnalysisType == 'temporal' ? 'Temporal Analysis' : (selectedAnalysisType == 'comparison' ? 'Cryptocurrency Comparison' : 'Cluster Analysis'))}</h5>
                    <p>${metricDescription}</p>
                </div>

                <!-- Image display area -->
                <div class="row">
                    <c:choose>
                        <c:when test="${not empty images}">
                            <c:forEach items="${images}" var="image" varStatus="status">
                                <div class="col-md-12 mb-4">
                                    <div class="chart-container-title">
                                        ${image.title}
                                    </div>
                                    <div class="blockchain-image-container">
                                        <img src="${contextPath}${image.path}" alt="${image.title}">
                                    </div>
                                    <div class="chart-info">
                                        ${image.description}
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col-12">
                                <div class="no-chart-available">
                                    <i class="fas fa-chart-bar mr-2"></i>
                                    No charts available for the current metric and analysis type
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <footer class="footer">
        <div class="container-fluid">
            <p class="mb-0">&copy; 2025 Zhongxing Du. All Rights Reserved.</p>
        </div>
    </footer>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 为指标选项卡添加点击事件监听
            const metricTabs = document.querySelectorAll('.metric-tab');
            metricTabs.forEach(tab => {
                tab.addEventListener('click', function(e) {
                    metricTabs.forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                });
            });
            
            // 调试信息输出
            console.log("当前分析类型: ${selectedAnalysisType}");
            console.log("当前指标: ${selectedMetric}");
            console.log("当前加密货币: ${selectedCryptocurrency}");
        });
    </script>
</body>
</html>