package com.example.demoai.bean;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class CourseQuery {
    @ToolParam(description = "课程类型：编程、设计、自媒体、其他", required = false)
    private String type;
    @ToolParam(description = "学历要求：0-无、1-初中、2-高中、3-大专、4-本科及以上", required = false)
    private Integer edu;
}
