package cn.matcheasy.project.modules.demo.exam.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @class: Exam
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Data
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String exam_id;

    private String exam_name;

    private Boolean delFlag;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String create_time;

    private String logo_path;

}
