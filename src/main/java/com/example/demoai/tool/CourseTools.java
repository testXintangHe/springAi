package com.example.demoai.tool;

import com.example.demoai.bean.Course;
import com.example.demoai.bean.CourseQuery;
import com.example.demoai.bean.School;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseTools {

    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourses(@ToolParam(description = "查询条件") CourseQuery query) {
        if (query == null) {
            return new ArrayList<>();
        }

        List<Course> result = new ArrayList<>();

        Course course1 = Course.builder()
                .id(1)
                .name("自媒体运用")
                .edu(1)
                .type("自媒体")
                .price(1000L)
                .duration(60)
                .build();
        result.add(course1);

        Course course2 = Course.builder()
                .id(2)
                .name("编程")
                .edu(3)
                .type("编程")
                .price(1000L)
                .duration(60)
                .build();
        result.add(course2);
        return result;
    }

    @Tool(description = "查询所有校区")
    public List<School> querySchool() {
        List<School> result = new ArrayList<>();
        School school1 = School.builder()
                .id(1)
                .name("杭州校区")
                .city("杭州")
                .build();
        result.add(school1);

        School school2 = School.builder()
                .id(2)
                .name("北京校区")
                .city("北京")
                .build();
        result.add(school2);

        School school3 = School.builder()
                .id(3)
                .name("西安校区")
                .city("西安")
                .build();
        result.add(school3);
        return result;
    }

    @Tool(description = "生成预约单，返回预约单号")
    public Integer createCourseReservation(@ToolParam(description = "预约课程") String course,
                                           @ToolParam(description = "学生姓名") String studentName,
                                           @ToolParam(description = "联系电话") String contactInfo,
                                           @ToolParam(description = "备注", required = false) String remark) {
        // 实际业务逻辑自己写
        return 1;
    }
}
