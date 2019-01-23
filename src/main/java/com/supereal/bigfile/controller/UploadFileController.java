package com.supereal.bigfile.controller;

import com.supereal.bigfile.form.FileForm;
import com.supereal.bigfile.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;

/**
 * Create by tianci
 * 2019/1/10 15:41
 */
@RestController
@RequestMapping("/file")
public class UploadFileController {

    @Autowired
    UploadFileService uploadFileService;


    @GetMapping("/open")
    public ModelAndView open() {

        return new ModelAndView("upload");
    }

    @PostMapping("/isUpload")
    public Map<String, Object> isUpload(@Valid FileForm form) {

        return uploadFileService.findByFileMd5(form.getMd5());

    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@Valid FileForm form,
                                      @RequestParam(value = "data", required = false)MultipartFile multipartFile) {
        Map<String, Object> map = null;

        try {
            map = uploadFileService.realUpload(form, multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
