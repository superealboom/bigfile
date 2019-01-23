package com.supereal.bigfile.form;

import lombok.Data;

/**
 * Create by tianci
 * 2019/1/10 16:33
 */
@Data
public class FileForm {

    private String md5;

    private String uuid;

    private String date;

    private String name;

    private String size;

    private String total;

    private String index;

    private String action;

    private String partMd5;
}
