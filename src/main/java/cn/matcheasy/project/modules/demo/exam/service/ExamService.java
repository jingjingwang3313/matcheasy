package cn.matcheasy.project.modules.demo.exam.service;

import cn.matcheasy.project.modules.demo.exam.entity.Exam;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @class: ExamService
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public interface ExamService {

    Exam select(String exam_id) throws Exception;

    void update(Exam exam) throws Exception;

    void delete(String exam_id) throws Exception;

    Future<List> getList() throws Exception;

    void testDS1() throws Exception;

    void testDS2() throws Exception;
}
