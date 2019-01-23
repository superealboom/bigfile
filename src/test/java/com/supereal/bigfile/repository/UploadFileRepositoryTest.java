package com.supereal.bigfile.repository;

import com.supereal.bigfile.dataobject.UploadFile;
import com.supereal.bigfile.utils.KeyUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Create by tianci
 * 2019/1/10 15:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadFileRepositoryTest {

    @Autowired
    UploadFileRepository repository;

    @Test
    public void save() {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileId(KeyUtil.genUniqueKey());
        uploadFile.setFileMd5("123");
        uploadFile.setFileName("123");
        uploadFile.setFilePath("123");
        uploadFile.setFileSize("123");
        uploadFile.setFileSuffix("123");

        repository.save(uploadFile);

    }

    @Test
    public void findByFileMd5() {
        UploadFile uploadFile = repository.findByFileMd5("123");
        Assert.assertNotNull(uploadFile);
    }
}