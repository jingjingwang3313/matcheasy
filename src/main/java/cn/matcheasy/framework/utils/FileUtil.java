package cn.matcheasy.framework.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @class FileUtil
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: 尽量使用commons, guava, hutool等已集成第三方工具类。
 */
public class FileUtil {

    /**
     * 避免工具类实例化
     */
    private FileUtil() {
    }

    public static void zipCompress(String zipSavePath, String zipName, File sourceFile) throws Exception {
        File fileFolder = new File(zipSavePath);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        zipSavePath += zipName;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipSavePath));
            compress(zos, sourceFile, zipName.substring(0, zipName.lastIndexOf(".")));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    private static void compress(ZipOutputStream zos, File sourceFile, String fileName) throws Exception {
        FileInputStream fis = null;
        try {
            if (sourceFile.isDirectory()) {
                File[] fileList = sourceFile.listFiles();
                if (fileList.length == 0) {
                    zos.putNextEntry(new ZipEntry(fileName + File.separator));
                } else {
                    for (File file : fileList) {
                        compress(zos, file, fileName + File.separator + file.getName());
                    }
                }
            } else {
                if (!sourceFile.exists()) {
                    zos.putNextEntry(new ZipEntry(File.separator));

                } else {
                    zos.putNextEntry(new ZipEntry(fileName));
                    fis = new FileInputStream(sourceFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zos != null) {
                zos.closeEntry();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static void downloadHttpUrl(String url, String dir, String fileName) throws Exception {
        URL httpurl = new URL(url);
        File dirfile = new File(dir);
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
        FileUtils.copyURLToFile(httpurl, new File(dir + fileName));
    }


    public static void deleteFileAll(File file) throws Exception {
        if (file.exists()) {
            File files[] = file.listFiles();
            int len = files.length;
            for (int i = 0; i < len; i++) {
                if (files[i].isDirectory()) {
                    deleteFileAll(files[i]);
                } else {
                    files[i].delete();
                }
            }
            file.delete();
        }
    }

    public static void mkdirsIfNotExist(String dirPath) throws Exception {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName, File
            file) throws Exception {
        if (file.exists()) {
            OutputStream out = null;
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                fileName = URLEncoder.encode(fileName, "UTF-8");
                String mineType = request.getServletContext().getMimeType(fileName);
                response.setContentType(mineType);
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";" + "filename*=utf-8''" + fileName);
                out = response.getOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        }
    }

    public static void deleteFileOrDirFile(String filePath) throws Exception {
        if (StringUtils.isNotBlank(filePath)) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                FileUtils.deleteQuietly(file);
            } else if (file.exists() && file.isDirectory()) {
                deleteFileAll(file);
            }
        }
    }

    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath + fileName);
            out.write(file);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void copyFile(String oldFile, String newPath, String fileName) throws Exception {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File oldfile = new File(oldFile);
        if (oldfile.isDirectory() || !oldfile.isFile()) {
            throw new Exception("【" + oldFile + "】 不是文件!");
        }
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                File new_Path = new File(newPath);
                if (!new_Path.exists()) {
                    new_Path.mkdirs();
                }
                //目标文件名
                if (StringUtils.isBlank(fileName)) {
                    fileName = cn.hutool.core.io.FileUtil.getName(oldfile);
                }
                String newFile = newPath + fileName;
                //读入原文件
                inputStream = new FileInputStream(oldFile);
                fileOutputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[1444];
                while ((byteread = inputStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fileOutputStream.write(buffer, 0, byteread);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void copyFile(File srcFile, File destFile) throws Exception {
        FileUtils.copyFile(srcFile, destFile);
    }

    public static void copyDirectory(File srcDir, File destDir) throws Exception {
        FileUtils.copyDirectory(srcDir, destDir);
    }

}
