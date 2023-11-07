package cn.matcheasy.framework.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.matcheasy.framework.commonbean.SheetBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * @class ExcelUtil
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: TODO
 */
@Slf4j
public class ExcelUtil {
    
    /**
     * 避免工具类实例化
     */
    private ExcelUtil() {
    }
    
    /**
     * Handled by wangjing at 2020/12/28/0028 17:04
     *
     * @Desc EXCEL导入
     */
    public static List<Map<String, Object>> importExcel(String filePath, int sheetIndex) throws Exception {
        ExcelReader excelReader = cn.hutool.poi.excel.ExcelUtil.getReader(filePath, sheetIndex);// 默认处理0第一个sheet,指定sheetIndex则处理指定sheet
        FileUtil.deleteFileOrDirFile(filePath);
        List<Map<String, Object>> readAll = excelReader.readAll();
        return readAll;
    }
    
    /**
     * Handled by wangjing at 2020/12/29/0029 13:01
     *
     * @Desc 大数据量Excel导入
     */
    public static void importBigExcel(String filePath, int sheetIndex) throws Exception {
        Excel07SaxReader reader = new Excel07SaxReader(rowHandler());
        reader.read(filePath, sheetIndex);// 如果sheetIndex为-1处理所有的sheet,指定sheetIndex则处理指定sheet
        FileUtil.deleteFileOrDirFile(filePath);
    }
    
    public static RowHandler rowHandler() {
        return new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowlist) {
                log.info("[{}] [{}] {}", sheetIndex, rowIndex, rowlist);
            }
        };
    }
    
    /**
     * Handled by wangjing at 2020/12/28/0028 17:04
     *
     * @Desc EXCEL导出
     */
    public static void exportExcel(HttpServletResponse response, List<Map<String, Object>> rows, String fileName) throws Exception {
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        writer.write(rows, true);
        ServletOutputStream out = null;
        try {
            fileName = UrlUtil.getEncodeURL(fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";" + "filename*=utf-8''" + fileName);
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        IoUtil.close(out);
    }
    
    /**
     * Handled by wangjing at 2020/12/29/0029 13:01
     *
     * @Desc 大数据量Excel导出
     */
    public static void exportBigExcel(HttpServletResponse response, List<Map<String, Object>> rows, String fileName) throws Exception {
        ExcelWriter bigWriter = cn.hutool.poi.excel.ExcelUtil.getBigWriter();
        bigWriter.write(rows, true);
        ServletOutputStream out = null;
        try {
            fileName = UrlUtil.getEncodeURL(fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";" + "filename*=utf-8''" + fileName);
            out = response.getOutputStream();
            bigWriter.flush(out, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bigWriter.close();
        }
        IoUtil.close(out);
    }
    
    /**
     * 大数据量Excel导出多个 Sheet 页
     *
     * @param response
     * @param sheetList 页数据
     * @param fileName  文件名
     */
    public static void exportMoreSheetBigExcel(HttpServletResponse response, List<SheetBean> sheetList, String fileName) throws Exception {
        ExcelWriter bigWriter = cn.hutool.poi.excel.ExcelUtil.getBigWriter();
        // 重命名第一个Sheet的名称，不然会默认多出一个Sheet1的页
        bigWriter.renameSheet(0, sheetList.get(0).getSheetName());
        for (SheetBean sheet : sheetList) {
            // 指定要写出的 Sheet 页
            bigWriter.setSheet(sheet.getSheetName());
            Integer[] columnWidth = sheet.getColumnWidth();
            if (columnWidth == null || columnWidth.length != sheet.getFieldAndAlias().size()) {
                // 设置默认宽度
                for (int i = 0; i < sheet.getFieldAndAlias().size(); i++) {
                    bigWriter.setColumnWidth(i, 25);
                }
            } else {
                // 设置自定义宽度
                for (int i = 0; i < columnWidth.length; i++) {
                    bigWriter.setColumnWidth(i, columnWidth[i]);
                }
            }
            // 设置字段和别名
            bigWriter.setHeaderAlias(sheet.getFieldAndAlias());
            // 设置只导出有别名的字段
            bigWriter.setOnlyAlias(true);
            // 设置默认行高
            bigWriter.setDefaultRowHeight(18);
            // 设置冻结行
            bigWriter.setFreezePane(1);
            // 一次性写出内容，使用默认样式，强制输出标题
            bigWriter.write(sheet.getCollection(), true);
            // 设置所有列为自动宽度，不考虑合并单元格
            // bigWriter.autoSizeColumnAll();
        }
        ServletOutputStream out = null;
        try {
            fileName = UrlUtil.getEncodeURL(fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";" + "filename*=utf-8''" + fileName);
            out = response.getOutputStream();
            bigWriter.flush(out, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bigWriter.close();
        }
        IoUtil.close(out);
    }
    
    public static void main(String[] args) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream("D:/qrcode/test.xlsx"));
        XSSFSheet sheet = null;
        String sheetName = null;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            sheet = wb.getSheetAt(i);
            sheetName = sheet.getSheetName();
            log.info("==========" + sheetName + "==========");
            // 根据sheet名称进行sheet导入
            ExcelReader excelReader = cn.hutool.poi.excel.ExcelUtil.getReader(cn.hutool.core.io.FileUtil.file("D:/qrcode/test.xlsx"), sheetName);
            List<Map<String, Object>> readAll = excelReader.readAll();
            for (int i1 = readAll.size() - 1; i1 >= 0; i1--) {
                Map<String, Object> stringObjectMap = readAll.get(i1);
                String region = MapUtil.getStr(stringObjectMap, "区域");
                String num = MapUtil.getStr(stringObjectMap, "题号");
                String link = MapUtil.getStr(stringObjectMap, "链接");
                if (cn.hutool.core.io.FileUtil.exist("D:/qrcode/img/" + num + ".png")) {
                    QrCodeUtil.generate(link, QrConfig.create().setImg("D:/qrcode/img/" + num + ".png"), cn.hutool.core.io.FileUtil.file("D:/qrcode/" + sheetName + "/" + sheetName + "_" + region + "_" + num + ".jpg"));
                } else {
                    QrCodeUtil.generate(link, QrConfig.create().setImg("D:/qrcode/img/ezha.jpg"), cn.hutool.core.io.FileUtil.file("D:/qrcode/" + sheetName + "/" + sheetName + "_" + region + "_" + num + ".jpg"));
                }
                log.info(sheetName + "_" + region + "_" + num + ", " + "生成");
            }
        }
        FileUtil.zipCompress("D:/", "qrcode.zip", new File("D:/qrcode/main/11111"));
        FileUtil.zipCompress("D:/", "qrcode.zip", new File("D:/qrcode/"));
    }
    
}
