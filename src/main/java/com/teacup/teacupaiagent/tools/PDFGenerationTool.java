package com.teacup.teacupaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.teacup.teacupaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * PDFGenerationTool 类
 * 用于生成PDF文件的工具类
 */
public class PDFGenerationTool {




    /**
     * 生成PDF文件的方法
     * @param fileName 要保存的PDF文件名
     * @param content 要包含在PDF中的内容
     * @return 返回生成结果的状态信息
     */
    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        // 构建PDF文件的保存目录路径
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        // 构建完整的文件路径
        String filePath = fileDir + "/" + fileName;
        try {
            
            // 创建PDF文件所在的目录
            FileUtil.mkdir(fileDir);
            
            // 使用try-with-resources确保资源正确关闭
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                




                
                // 创建中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                // 设置文档字体
                document.setFont(font);
                
                // 创建包含内容的段落
                Paragraph paragraph = new Paragraph(content);
                
                // 将段落添加到文档中
                document.add(paragraph);
            }
            // 返回成功信息
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            // 返回错误信息
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
