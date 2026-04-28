package com.teacup.teacupaiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import org.springframework.ai.chat.client.advisor.api.Advisor;

/**
 * 自定义关于知识库服务的RAG增强顾问工厂
 */
public class WorkAppRagCustomAdvisorFactory {


    /**
     * 创建一个用于工作应用的自定义顾问对象
     *
     * @param vectorStore 向量存储对象，用于存储和检索相关数据
     * @param status 顾问的状态信息，用于标识顾问的当前状态
     * @return 返回一个配置好的顾问对象(Advisor)，该顾问专门用于爱情应用场景
     */
    public static Advisor createWorkAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 创建过滤器
//        Filter.Expression expression = new FilterExpressionBuilder()
//                .eq("status", status)
//                .build();
        // 创建文档检索器
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
//                .filterExpression(expression)
                .similarityThreshold(0.5)
                .topK(3)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(WorkAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }


}
