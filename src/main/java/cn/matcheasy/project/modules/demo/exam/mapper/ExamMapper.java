package cn.matcheasy.project.modules.demo.exam.mapper;

import cn.matcheasy.project.modules.demo.exam.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @class: ExamMapper
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Mapper
public interface ExamMapper {

    List<Exam> findAll(Map paramMap);

    Exam select(@Param("exam_id") String exam_id);

    void update(Exam exam);

    void delete(String exam_id);
}
