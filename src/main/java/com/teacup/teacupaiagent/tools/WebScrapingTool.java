package com.teacup.teacupaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * WebScrapingTool类是一个网页抓取工具，用于获取网页内容
 * 使用了Jsoup库来解析和获取HTML文档
 */
public class WebScrapingTool {

    /**
     * 抓取指定URL的网页内容
     * @param url 要抓取的网页URL地址
     * @return 返回网页的HTML内容，如果发生错误则返回错误信息
     */
    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            // 使用Jsoup连接到指定URL并获取HTML文档
            Document doc = Jsoup.connect(url).get();
            // 返回完整的HTML内容
            return doc.html();
        } catch (IOException e) {
            // 捕获并处理可能发生的IO异常，返回错误信息
            return "Error scraping web page: " + e.getMessage();
        }
    }
}