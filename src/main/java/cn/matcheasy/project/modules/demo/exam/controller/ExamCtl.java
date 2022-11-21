package cn.matcheasy.project.modules.demo.exam.controller;

import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.MyAssert;
import cn.matcheasy.project.modules.demo.exam.entity.Exam;
import cn.matcheasy.project.modules.demo.exam.service.ExamService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @class: ExamCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "项目CRUD示例")
@RestController
@RequestMapping("/exam")
public class ExamCtl
{

    @Autowired
    private ExamService examService;

    @ApiOperation(value = "考试查询", notes = "根据ID查询考试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exam_id", value = "考试ID", required = true, dataType = "int", example = "123")
    })
    @GetMapping(value = "/select/{exam_id}")
    public Result select(HttpServletRequest request, @PathVariable("exam_id") String exam_id) throws Exception
    {
        Map resultMap = new HashMap();
        Map paramMap = ComU.getRequestMap(request);
        /*if (StringUtils.isEmpty(exam_id))
        {
            return Result.error("考试ID exam_id 不能为空！");
        }*/
        MyAssert.isNotBlank(exam_id, "考试ID exam_id 不能为空！");
        log.info("开始查询...");
        Exam exam = examService.select(exam_id);
        resultMap.put(ProjectConst.RESULT, exam);
        return Result.success(resultMap);
    }

    @ApiOperation(value = "考试新增/编辑", notes = "考试新增编辑操作,考试ID存在则新增，反之编辑")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok", response = Exam.class)
    })
    @PostMapping(value = "/edit")
    public Result edit(HttpServletRequest request, @RequestBody Exam exam) throws Exception
    {
        Map resultMap = new HashMap();
        Map paramMap = ComU.getRequestMap(request);
        log.info("开始编辑...");
        //if (StringUtils.isEmpty(exam.getExam_id())) {
        //    exam.setExam_id(UUIDUtil.fastSimpleUUID());
        //}
        examService.update(exam);
        return Result.success("更新成功！");
    }

    @ApiOperation(value = "考试删除", notes = "根据ID删除考试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exam_id", value = "考试ID", required = true, dataType = "int", example = "123")
    })
    @DeleteMapping(value = "/delete")
    public Result delete(HttpServletRequest request, @RequestParam("exam_id") String exam_id) throws Exception
    {
        Map resultMap = new HashMap();
        Map paramMap = ComU.getRequestMap(request);
        /*if (StringUtils.isEmpty(exam_id))
        {
            return Result.error("考试ID exam_id 不能为空！");
        }*/
        MyAssert.isNotBlank(exam_id, "考试ID exam_id 不能为空！");
        log.info("开始删除...");
        examService.delete(exam_id);
        return Result.success("删除成功！");
    }

    @ApiOperation(value = "异步多线程调用示例", notes = "")
    @GetMapping(value = "/getList")
    public Result getList(HttpServletRequest request) throws Exception
    {
        Map resultMap = new HashMap();
        Future<List> future = examService.getList();
        List list = future.get();
        log.info(list.toString());
        resultMap.put(ProjectConst.RESULT, list);
        return Result.success(resultMap);
    }

    @ApiOperation(value = "测试动态数据源", notes = "")
    @GetMapping(value = "/testDS")
    public Result testDS(HttpServletRequest request) throws Exception
    {
        examService.testDS1();
        //examService.testDS2();
        return Result.success();
    }

}
