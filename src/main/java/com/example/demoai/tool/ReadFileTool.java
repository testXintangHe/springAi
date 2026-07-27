package com.example.demoai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ReadFileTool {
    @Tool(name = "readFile", description = "根据文件名称，查询文件内容")
    public String readFile(@ToolParam(description = "文件名称") String fileName) {
        System.out.println(fileName);
        return "测试内容";
    }
}
