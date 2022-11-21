package cn.matcheasy.project.common.api.file.service;

import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.FileUtil;
import cn.matcheasy.framework.utils.JwtUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import cn.matcheasy.project.common.api.file.entity.SysFile;
import cn.matcheasy.project.common.api.file.mapper.SysFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * @class: FileServiceImpl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService
{

    @Value("${server.port}")
    private String port;

    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${virtualPath}")
    private String virtualPath;

    @Autowired
    private SysFileMapper sysFileMapper;

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    @Override
    public Result uploadFile(HttpServletRequest request, String dirName) throws Exception
    {
        String access_token = MapUtil.getStr(ComU.getRequestMap(request), ProjectConst.ACCESS_TOKEN);
        long userId = 0;
        if (!StringUtils.isEmpty(access_token))
        {
            userId = JwtUtil.getUserId(access_token);
        }
        //StringBuffer sb = new StringBuffer("").append(userId).append("/");
        Map returnMap = new HashMap();
        List list = new ArrayList<Map>();
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断 request 是否有文件上传
        if (multipartResolver.isMultipart(request))
        {
            // 访问url
            String fileAccessUrl = null;
            // 存储url
            String fileStorageUrl = null;
            String newName = null;
            // 转换成多部分request上传
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 上传文件name不同,获取多个文件名
            Iterator<String> it = multiRequest.getFileNames();
            if (!it.hasNext())
            {
                return Result.error("请选择上传文件!");
            }
            SysFile sysFile = new SysFile();
            sysFile.setCreateBy(userId);
            while (it.hasNext())
            {
                // 根据文件名获取文件
                MultipartFile multipartFile = multiRequest.getFile(it.next());
                //取得上传文件
                if (ObjectUtils.isNotEmpty(multipartFile))
                {
                    sysFile.setFileId(null);//清空ID
                    //取得当前上传文件的文件名称
                    String fileName = multipartFile.getOriginalFilename();
                    sysFile.setFileName(fileName);
                    //如果名称不为空,说明该文件存在，否则说明该文件不存在
                    if (StringUtils.isNotBlank(StringUtils.trim(fileName)))
                    {
                        Map map = new LinkedHashMap();
                        //重命名
                        String extName = fileName.substring(fileName.lastIndexOf("."));
                        newName = UUIDUtil.fastSimpleUUID() + extName;
                        sysFile.setFileRealName(newName);
                        FileUtil.uploadFile(multipartFile.getBytes(), uploadPath + dirName + File.separator, newName);
                        fileAccessUrl = request.getScheme() + "://" + request.getServerName() + ":" + port + virtualPath + dirName + File.separator + newName;
                        sysFile.setAccessUrl(fileAccessUrl);
                        fileStorageUrl = uploadPath + dirName + File.separator + newName;
                        sysFile.setStoragePath(fileStorageUrl);
                        sysFile.setRelativePath(dirName + File.separator + newName);
                        sysFile.setFileType(cn.hutool.core.io.FileUtil.extName(fileName));
                        sysFile.setFileSize(multipartFile.getSize());
                        sysFileMapper.insert(sysFile);
                        log.info("======================上传成功============================");
                        log.info("文件ID:【{}】", sysFile.getFileId());
                        log.info("原文件名称:【{}】", fileName);
                        log.info("新生成名称:【{}】", newName);
                        log.info("访问路径为:【{}】", fileAccessUrl);
                        log.info("存储路径为:【{}】", fileStorageUrl);
                        log.info("========================================================");
                        map.put("fileId", sysFile.getFileId());
                        map.put("fileName", fileName);
                        map.put("realName", newName);
                        map.put(ProjectConst.FILE_ACCESS_URL, fileAccessUrl);
                        map.put(ProjectConst.FILE_STORAGE_URL, fileStorageUrl);
                        list.add(map);
                    }
                }
            }
        }
        returnMap.put(ProjectConst.RESULT, list);
        return Result.success(returnMap);
    }

    @Override
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileId) throws Exception
    {
        SysFile sysFile = sysFileMapper.selectByPrimaryKey(Long.valueOf(fileId));
        if (ObjectUtils.isEmpty(sysFile))
        {
            throw new Exception("文件不存在！");
        }
        File file = cn.hutool.core.io.FileUtil.file(sysFile.getStoragePath());
        FileUtil.downloadFile(request, response, sysFile.getFileName(), file);
    }

}
