package com.example.demoai.bean;


import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author huge
 * @since 2025-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseReservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 预约课程
     */
    private String course;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 预约校区
     */
    private String school;

    /**
     * 备注
     */
    private String remark;


}
