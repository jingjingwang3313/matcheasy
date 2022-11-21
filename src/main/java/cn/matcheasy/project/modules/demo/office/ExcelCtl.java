package cn.matcheasy.project.modules.demo.office;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.matcheasy.framework.commonbean.SheetBean;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.ExcelUtil;
import cn.matcheasy.framework.utils.FileUtil;
import cn.matcheasy.project.common.api.file.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @class: OfficeCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "excle操作示例")
@RestController
@RequestMapping("/anon/office")
public class ExcelCtl
{

    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${otherPath}")
    private String otherPath;

    @Value("${tempPath}")
    private String tempPath;

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "Excel导入", notes = "")
    @ApiImplicitParam(name = "dirName", value = "文件上传目录名", required = true, dataType = "String")
    @PostMapping("/importExcel")
    public Result importExcel(HttpServletRequest request, HttpServletResponse response, String dirName) throws Exception
    {
        Map retMap = new HashMap();
        if (StringUtils.isEmpty(dirName))
        {
            return Result.error("文件上传目录名 dirName 参数缺失！");
        }
        Result result = fileService.uploadFile(request, dirName);
        List<Map> resultList = ComU.getResultDataToList(result);
        if (CollectionUtils.isNotEmpty(resultList))
        {
            resultList.forEach(map ->
            {
                List<Map<String, Object>> list = null;
                try
                {
                    list = ExcelUtil.importExcel(MapUtil.getStr(map, ProjectConst.FILE_STORAGE_URL), 0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                log.info("导入Excel数据: [{}] ", list.toString());
            });
        }
        return Result.success("导入成功!");
    }

    @ApiOperation(value = "大数据量Excel导入", notes = "")
    @ApiImplicitParam(name = "dirName", value = "文件上传目录名", required = true, dataType = "String")
    @PostMapping("/importBigExcel")
    public Result importBigExcel(HttpServletRequest request, HttpServletResponse response, String dirName) throws Exception
    {
        Map retMap = new HashMap();
        if (StringUtils.isEmpty(dirName))
        {
            return Result.error("文件上传目录名 dirName 参数缺失！");
        }
        Result result = fileService.uploadFile(request, dirName);
        List<Map> resultList = ComU.getResultDataToList(result);
        if (CollectionUtils.isNotEmpty(resultList))
        {
            resultList.forEach(map ->
            {
                try
                {
                    ExcelUtil.importBigExcel(MapUtil.getStr(map, ProjectConst.FILE_STORAGE_URL), 0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        }
        return Result.success("导入成功！");
    }

    @ApiOperation(value = "Excel导出", notes = "")
    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        List<Map> examList = new ArrayList<Map>();//由service查询要导出的数据集合,元数据是map或bean
        ArrayList<Map<String, Object>> rows = CollUtil.newArrayList();
        Map<String, Object> m = new LinkedHashMap<>();//使用有序Map
        examList.parallelStream().forEach(map ->
        {
            m.put("姓名", MapUtil.getStr(map, "name"));
            m.put("年龄", MapUtil.getInt(map, "age"));
            m.put("日期", MapUtil.getInt(map, "date"));
            rows.add(m);
        });
        m.put("姓名", "阿尔法狗");
        m.put("年龄", 23);
        m.put("日期", DateUtil.date());
        rows.add(m);
        ExcelUtil.exportExcel(response, rows, "文件名.xlsx");
    }

    @ApiOperation(value = "大数据量Excel导出", notes = "")
    @GetMapping("/exportBigExcel")
    public void exportBigExcel(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        List<Map> examList = new ArrayList<Map>();//由service查询要导出的数据集合,元数据是map或bean
        ArrayList<Map<String, Object>> rows = CollUtil.newArrayList();
        Map<String, Object> m = new LinkedHashMap<>();//使用有序Map
        examList.parallelStream().forEach(map ->
        {
            m.put("姓名", MapUtil.getStr(map, "name"));
            m.put("年龄", MapUtil.getInt(map, "age"));
            m.put("日期", MapUtil.getInt(map, "date"));
            rows.add(m);
        });
        m.put("姓名", "阿尔法狗");
        m.put("年龄", 23);
        m.put("日期", DateUtil.date());
        rows.add(m);
        ExcelUtil.exportBigExcel(response, rows, "文件名.xlsx");
    }

    @ApiOperation(value = "大数据量Excel多sheet导出", notes = "")
    @GetMapping("/exportMoreSheetBigExcel")
    public void exportMoreSheetBigExcel(HttpServletResponse response) throws Exception
    {
        List<SheetBean> sheetList = new LinkedList<SheetBean>();

        ArrayList<Map<String, Object>> rows = CollUtil.newArrayList();
        for (int i = 0; i < 15; i++)
        {
            // 可用无序Map,不影响单元格先后顺序
            Map m = new LinkedHashMap<>();
            m.put("name", "阿尔法狗" + i);
            m.put("age", 23 + i);
            m.put("datetime", DateUtil.date());
            rows.add(m);
        }

        //标题和列属性顺序要对应
        String[] titles = new String[]{"姓名", "年龄", "日期"};
        String[] props = new String[]{"name", "age", "datetime"};

        SheetBean sheet1 = new SheetBean();
        sheet1.setTitles(titles);
        sheet1.setProperties(props);
        sheet1.setSheetName("第一个sheet");
        sheet1.setCollection(rows);
        sheetList.add(sheet1);

        SheetBean sheet2 = new SheetBean();
        sheet2.setTitles(titles);
        sheet2.setProperties(props);
        sheet2.setSheetName("第二个sheet");
        sheet2.setCollection(rows);
        sheetList.add(sheet2);

        ExcelUtil.exportMoreSheetBigExcel(response, sheetList, "文件名.xlsx");
    }

    @ApiOperation(value = "导出多文件压缩包", notes = "")
    @GetMapping("/fileZip")
    public void fileZip(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        File zipDir = new File(uploadPath + tempPath);
        String zipFileName = DateUtil.today() + ".zip";
        FileUtil.zipCompress(uploadPath + otherPath, zipFileName, zipDir);
        FileUtil.downloadFile(request, response, zipFileName, new File(uploadPath + otherPath + zipFileName));
        FileUtil.deleteFileAll(new File(uploadPath + tempPath));
        FileUtil.deleteFileAll(new File(uploadPath + otherPath));
    }

}
