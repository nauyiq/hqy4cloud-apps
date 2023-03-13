package com.hqy.cloud.apps.blog.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.hqy.cloud.apps.blog.vo.UploadFileVO;
import com.hqy.cloud.apps.commom.constants.AppsConstants;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.file.FileValidateContext;
import com.hqy.foundation.common.FileResponse;
import com.hqy.web.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.hqy.cloud.common.result.ResultCode.INVALID_FILE_TYPE;
import static com.hqy.cloud.common.result.ResultCode.INVALID_UPLOAD_FILE;

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

    @PostMapping("/admin/blog/upload/image")
    public R<UploadFileVO> uploadImage(@RequestParam("file") MultipartFile file) {
        AssertUtil.notNull(file, "Upload file should not be null.");
        FileResponse response = uploadFileService.uploadImgFile(AppsConstants.Blog.UPLOAD_IMAGE_FOLDER, file);
        if (!response.result()) {
            return R.failed(response.message(), INVALID_UPLOAD_FILE.code);
        }
        return R.ok(new UploadFileVO(response.path(), response.relativePath()));
    }

    @PostMapping("/blog/upload/avatar")
    public R<UploadFileVO> uploadAvatar(@RequestParam("file") MultipartFile avatar) {
        AssertUtil.notNull(avatar, "Upload avatar file should not be null.");
        FileResponse response = uploadFileService.uploadAvatar(avatar);
        if (!response.result()) {
            return R.failed(response.message(), INVALID_UPLOAD_FILE.code);
        }
        return R.ok(new UploadFileVO(response.path(), response.relativePath()));
    }

    @PostMapping("/admin/blog/upload/music")
    public R<UploadFileVO> uploadMusic(@RequestParam("file") MultipartFile musicFile) {
        AssertUtil.notNull(musicFile, "Upload music file file should not be null.");
        String originalFilename = musicFile.getOriginalFilename();
        String extName = FileNameUtil.extName(originalFilename);
        if (!FileValidateContext.isSupportedFile(FileValidateContext.SUPPORT_MEDIA_FILE_TYPES, extName)) {
            return R.failed(INVALID_FILE_TYPE);
        }
        FileResponse response = uploadFileService.uploadFile(AppsConstants.Blog.UPLOAD_IMAGE_MUSIC, musicFile);
        if (!response.result()) {
            return R.failed(response.message(), INVALID_UPLOAD_FILE.code);
        }
        return R.ok(new UploadFileVO(response.path(), response.relativePath()));
    }



}
