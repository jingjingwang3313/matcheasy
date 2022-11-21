package cn.matcheasy.project.common.api.error.controller;

import cn.matcheasy.framework.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: ErrorCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@RestController
@RequestMapping("/anon")
public class ErrorCtl {

    @GetMapping("/error/{code}/{message}")
    public Result error(@PathVariable("code") String code, @PathVariable("message") String message) throws Exception {
        return Result.error(code, message);
    }
}

