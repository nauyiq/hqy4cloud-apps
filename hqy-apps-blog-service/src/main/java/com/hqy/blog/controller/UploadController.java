package com.hqy.blog.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.hqy.apps.common.constants.AppsConstants;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.vo.UploadFileVO;
import com.hqy.foundation.common.FileResponse;
import com.hqy.util.AssertUtil;
import com.hqy.util.file.FileValidateContext;
import com.hqy.web.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 15:18
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadController {

    private final UploadFileService uploadFileService;

    @PostMapping("/upload/image")
    public DataResponse uploadImage(@RequestParam("file") MultipartFile file) {
        AssertUtil.notNull(file, "Upload file should not be null.");
        FileResponse response = uploadFileService.uploadImgFile(AppsConstants.Blog.UPLOAD_IMAGE_FOLDER, file);
        if (!response.result()) {
            return CommonResultCode.dataResponse(CommonResultCode.INVALID_UPLOAD_FILE, response.message());
        }
        return CommonResultCode.dataResponse(new UploadFileVO(response.path(), response.relativePath()));
    }

    @PostMapping("/upload/avatar")
    public DataResponse uploadAvatar(@RequestParam("file") MultipartFile avatar) {
        AssertUtil.notNull(avatar, "Upload avatar file should not be null.");
        FileResponse response = uploadFileService.uploadAvatar(avatar);
        if (!response.result()) {
            return CommonResultCode.dataResponse(CommonResultCode.INVALID_UPLOAD_FILE, response.message());
        }
        return CommonResultCode.dataResponse(new UploadFileVO(response.path(), response.relativePath()));
    }


    @PostMapping("/music")
    public DataResponse uploadMusic(@RequestParam("file") MultipartFile musicFile) {
        AssertUtil.notNull(musicFile, "Upload music file file should not be null.");
        String originalFilename = musicFile.getOriginalFilename();
        String extName = FileNameUtil.extName(originalFilename);
        if (!FileValidateContext.isSupportedFile(FileValidateContext.SUPPORT_MEDIA_FILE_TYPES, extName)) {
            return CommonResultCode.dataResponse(CommonResultCode.INVALID_FILE_TYPE);
        }
        FileResponse response = uploadFileService.uploadFile(AppsConstants.Blog.UPLOAD_IMAGE_MUSIC, musicFile);
        if (!response.result()) {
            return CommonResultCode.dataResponse(CommonResultCode.INVALID_UPLOAD_FILE, response.message());
        }
        return CommonResultCode.dataResponse(new UploadFileVO(response.path(), response.relativePath()));
    }



}
