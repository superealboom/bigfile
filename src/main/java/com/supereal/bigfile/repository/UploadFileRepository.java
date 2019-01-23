package com.supereal.bigfile.repository;

import com.supereal.bigfile.dataobject.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Create by tianci
 * 2019/1/10 15:02
 */
public interface UploadFileRepository extends JpaRepository<UploadFile, String> {

    UploadFile findByFileMd5(String fileMd5);

    void deleteByFileMd5(String fileMd5);
}
