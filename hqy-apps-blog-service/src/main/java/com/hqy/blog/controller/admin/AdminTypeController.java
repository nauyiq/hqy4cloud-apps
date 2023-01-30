package com.hqy.blog.controller.admin;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.blog.dto.TypeDTO;
import com.hqy.blog.service.request.ArticleTypeRequestService;
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
    public DataResponse articleTypes(String name, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 10 : size;
        return articleTypeRequestService.adminPageTypes(name, current, size);
    }

    @PostMapping("/type")
    public DataResponse addType(@Valid @RequestBody TypeDTO typeDTO) {
        return articleTypeRequestService.addType(typeDTO);
    }

    @PutMapping("/type")
    public DataResponse editType(@Valid @RequestBody TypeDTO typeDTO) {
        if (Objects.isNull(typeDTO.getId())) {
            return CommonResultCode.dataResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return articleTypeRequestService.editType(typeDTO);
    }

    @DeleteMapping("/type/{id}")
    public DataResponse deleteType(@PathVariable("id") Integer id) {
        if (Objects.isNull(id)) {
            return CommonResultCode.dataResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return articleTypeRequestService.deleteType(id);
    }



}
