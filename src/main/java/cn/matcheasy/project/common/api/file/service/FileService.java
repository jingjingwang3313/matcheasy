package cn.matcheasy.project.common.api.file.service;

import cn.matcheasy.framework.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @class: FileService
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public interface FileService {

    Result uploadFile(HttpServletRequest request, String dirName) throws Exception;

    void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileId) throws Exception;
}
