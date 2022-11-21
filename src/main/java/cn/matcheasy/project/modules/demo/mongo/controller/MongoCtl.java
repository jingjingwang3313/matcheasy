package cn.matcheasy.project.modules.demo.mongo.controller;

import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.modules.demo.mongo.service.MongoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: MongoCtl
 * @author: wangjing
 * @date: 2021/6/28/0028
 * @desc: TODO
 */
@Slf4j
@Api(tags = "Mongo示例")
@RestController
@RequestMapping("/anon/mongo")
public class MongoCtl
{
    @Autowired
    private MongoService mongoService;

    @GetMapping("/save")
    public Result save()
    {
        return mongoService.save();
    }

    @GetMapping("/find")
    public Result find()
    {
        return mongoService.find();
    }

}
