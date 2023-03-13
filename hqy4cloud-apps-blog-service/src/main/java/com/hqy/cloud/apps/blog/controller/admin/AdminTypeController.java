package com.hqy.cloud.apps.blog.controller.admin;

import com.hqy.cloud.apps.blog.dto.TypeDTO;
import com.hqy.cloud.apps.blog.service.request.ArticleTypeRequestService;
import com.hqy.cloud.apps.blog.vo.ArticleTypeVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/4 15:23
 */
@Slf4j
@RestController
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminTypeController {

    private final ArticleTypeRequestService articleTypeRequestService;

    @GetMapping("/type/page")
    public R<PageResult<ArticleTypeVO>> articleTypes(String name, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return articleTypeRequestService.adminPageTypes(name, current, size);
    }

    @PostMapping("/type")
    public R<Boolean> addType(@Valid @RequestBody TypeDTO typeDTO) {
        return articleTypeRequestService.addType(typeDTO);
    }

    @PutMapping("/type")
    public R<Boolean> editType(@Valid @RequestBody TypeDTO typeDTO) {
        if (Objects.isNull(typeDTO.getId())) {
            return R.failed(ResultCode.ERROR_PARAM_UNDEFINED);
        }
        return articleTypeRequestService.editType(typeDTO);
    }

    @DeleteMapping("/type/{id}")
    public R<Boolean> deleteType(@PathVariable("id") Integer id) {
        return articleTypeRequestService.deleteType(id);
    }



}
