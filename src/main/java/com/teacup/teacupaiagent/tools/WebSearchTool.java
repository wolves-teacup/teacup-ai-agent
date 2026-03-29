package com.teacup.teacupaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *   WebSearchTool 类是一个用于执行网络搜索的工具类
 * 它提供了通过百度搜索引擎搜索信息的功能
 */
public class WebSearchTool {

    
    /**
     * 搜索API的基础URL，用于构建完整的搜索请求
     */
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    /**
     * 存储API密钥，用于身份验证
     */
    private final String apiKey;

    /**
     * 构造函数，初始化WebSearchTool实例
     * @param apiKey 用于身份验证的API密钥
     */
    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 使用百度搜索引擎执行网络搜索
     * @param query 搜索关键词，用于查找相关信息
     * @return 返回搜索结果的JSON字符串，最多包含前5个结果
     */
    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        // 创建参数映射，包含搜索查询和API密钥
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            // 发送HTTP GET请求获取搜索结果
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            
            // 解析JSON响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            
            // 获取有机搜索结果列表
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            // 只取前5个搜索结果
            List<Object> objects = organicResults.subList(0, 5);
            
            // 将结果转换为逗号分隔的JSON字符串
            String result = objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
            return result;
        } catch (Exception e) {
            // 处理搜索过程中的异常
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}