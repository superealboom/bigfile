package com.supereal.bigfile.service.Impl;

import com.supereal.bigfile.common.Constant;
import com.supereal.bigfile.dataobject.UploadFile;
import com.supereal.bigfile.form.FileForm;
import com.supereal.bigfile.repository.UploadFileRepository;
import com.supereal.bigfile.service.UploadFileService;
import com.supereal.bigfile.utils.FileMd5Util;
import com.supereal.bigfile.utils.KeyUtil;
import com.supereal.bigfile.utils.NameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by tianci
 * 2019/1/11 11:24
 */

@Service
public class UploadFileServiceImpl implements UploadFileService {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    UploadFileRepository uploadFileRepository;

    @Override
    public Map<String, Object> findByFileMd5(String md5) {
        UploadFile uploadFile = uploadFileRepository.findByFileMd5(md5);

        Map<String, Object> map = null;
        if (uploadFile == null) {
            //没有上传过文件
            map = new HashMap<>();
            map.put("flag", 0);
            map.put("fileId", KeyUtil.genUniqueKey());
            map.put("date", simpleDateFormat.format(new Date()));
        } else {
            //上传过文件，判断文件现在还存在不存在
            File file = new File(uploadFile.getFilePath());

            if (file.exists()) {
                if (uploadFile.getFileStatus() == 1) {
                    //文件只上传了一部分
                    map = new HashMap<>();
                    map.put("flag", 1);
                    map.put("fileId", uploadFile.getFileId());
                    map.put("date", simpleDateFormat.format(new Date()));
                } else if (uploadFile.getFileStatus() == 2) {
                    //文件早已上传完整
                    map = new HashMap<>();
                    map.put("flag" , 2);
                }
            } else {

                map = new HashMap<>();
                map.put("flag", 0);
                map.put("fileId", uploadFile.getFileId());
                map.put("date", simpleDateFormat.format(new Date()));
            }
        }
        return map;
    }


    @Override
    public Map<String, Object> realUpload(FileForm form, MultipartFile multipartFile) throws Exception {
        String action = form.getAction();
        String fileId = form.getUuid();
        Integer index = Integer.valueOf(form.getIndex());
        String partMd5 = form.getPartMd5();
        String md5 = form.getMd5();
        Integer total = Integer.valueOf(form.getTotal());
        String fileName = form.getName();
        String size = form.getSize();
        String suffix = NameUtil.getExtensionName(fileName);

        String saveDirectory = Constant.PATH + File.separator + fileId;
        String filePath = saveDirectory + File.separator + fileId + "." + suffix;
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }
        //文件分片位置
        File file = new File(saveDirectory, fileId + "_" + index);

        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        Map<String, Object> map = null;
        if ("check".equals(action)) {
            String md5Str = FileMd5Util.getFileMD5(file);
            if (md5Str != null && md5Str.length() == 31) {
                System.out.println("check length =" + partMd5.length() + " md5Str length" + md5Str.length() + "   " + partMd5 + " " + md5Str);
                md5Str = "0" + md5Str;
            }
            if (md5Str != null && md5Str.equals(partMd5)) {
                //分片已上传过
                map = new HashMap<>();
                map.put("flag", "1");
                map.put("fileId", fileId);
                if(index != total)
                    return map;
            } else {
                //分片未上传
                map = new HashMap<>();
                map.put("flag", "0");
                map.put("fileId", fileId);
                return map;
            }
        } else if("upload".equals(action)) {
            //分片上传过程中出错,有残余时需删除分块后,重新上传
            if (file.exists()) {
                file.delete();
            }
            multipartFile.transferTo(new File(saveDirectory, fileId + "_" + index));
            map = new HashMap<>();
            map.put("flag", "1");
            map.put("fileId", fileId);
            if(index != total)
                return map;
        }

        if (path.isDirectory()) {
            File[] fileArray = path.listFiles();
            if (fileArray != null) {
                if (fileArray.length == total) {
                    //分块全部上传完毕,合并

                    File newFile = new File(saveDirectory, fileId + "." + suffix);
                    FileOutputStream outputStream = new FileOutputStream(newFile, true);//文件追加写入
                    byte[] byt = new byte[10 * 1024 * 1024];
                    int len;
                    FileInputStream temp = null;//分片文件
                    for (int i = 0; i < total; i++) {
                        int j = i + 1;
                        temp = new FileInputStream(new File(saveDirectory, fileId + "_" + j));
                        while ((len = temp.read(byt)) != -1) {
                            outputStream.write(byt, 0, len);
                        }
                    }
                    //关闭流
                    temp.close();
                    outputStream.close();
                    //修改FileRes记录为上传成功
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFileId(fileId);
                    uploadFile.setFileStatus(2);
                    uploadFile.setFileName(fileName);
                    uploadFile.setFileMd5(md5);
                    uploadFile.setFileSuffix(suffix);
                    uploadFile.setFilePath(filePath);
                    uploadFile.setFileSize(size);

                    uploadFileRepository.save(uploadFile);

                    map=new HashMap<>();
                    map.put("fileId", fileId);
                    map.put("flag", "2");

                    return map;
                } else if(index == 1) {
                    //文件第一个分片上传时记录到数据库
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFileMd5(md5);
                    String name = NameUtil.getFileNameNoEx(fileName);
                    if (name.length() > 32) {
                        name = name.substring(0, 32);
                    }
                    uploadFile.setFileName(name);
                    uploadFile.setFileSuffix(suffix);
                    uploadFile.setFileId(fileId);
                    uploadFile.setFilePath(filePath);
                    uploadFile.setFileSize(size);
                    uploadFile.setFileStatus(1);

                    uploadFileRepository.save(uploadFile);
                }
            }
        }
        return map;
    }
}
