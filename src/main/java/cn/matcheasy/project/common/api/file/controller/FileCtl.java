package cn.matcheasy.project.common.api.file.controller;

import cn.matcheasy.framework.result.Result;
import cn.matcheasy.project.common.api.file.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @class: FileCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
public class FileCtl {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "上传文件", notes = "支持批量,返回文件对应的信息")
    @ApiImplicitParam(name = "dirName", value = "文件上传目录名", required = true, dataType = "String")
    @PostMapping("/upload")
    public Result upload(HttpServletRequest request, String dirName) throws Exception {
        if (StringUtils.isEmpty(dirName)) {
            return Result.error("文件上传目录名 dirName 参数缺失！");
        }
        Result result = fileService.uploadFile(request, dirName);
        return result;
    }

    @ApiOperation(value = "下载文件")
    @ApiImplicitParam(name = "fileId", value = "文件ID", required = true, dataType = "String")
    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response, String fileId) throws Exception {
        if (StringUtils.isEmpty(fileId)) {
            throw new Exception("文件ID fileId 参数缺失！");
        }
        fileService.downloadFile(request, response, fileId);
    }

}
