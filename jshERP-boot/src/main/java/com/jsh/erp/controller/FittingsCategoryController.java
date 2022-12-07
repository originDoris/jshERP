package com.jsh.erp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.FittingsCategory;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.datasource.query.FittingsCategoryQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.FittingsCategory.FittingsCategoryService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: CarModelController
 * @Description:
 * @date: 2022/12/7 14:46
 */
@RestController
@RequestMapping(value = "/category")
@Api(tags = {"配件类别"})
@Slf4j
public class FittingsCategoryController {
    @Resource
    private FittingsCategoryService fittingsCategoryService;

    @PostMapping("/save")
    public BaseResponseInfo save(@RequestBody FittingsCategory fittingsCategory) {
        BaseResponseInfo baseResponseInfo = new BaseResponseInfo();
        try {
            boolean result = fittingsCategoryService.save(fittingsCategory);
            baseResponseInfo.setData(result);
            baseResponseInfo.setCode(SERVICE_SUCCESS_CODE);
            return baseResponseInfo;
        } catch (Exception e) {
            log.info("保存数据出错", e);
            baseResponseInfo.setCode(SAVE_CAR_MODEL_FAIL_CODE);
            baseResponseInfo.setData(false);
            baseResponseInfo.setMsg(SAVE_CAR_MODEL_FAIL_MSG);
            return baseResponseInfo;
        }
    }


    @PostMapping("/modify")
    public BaseResponseInfo modify(@RequestBody FittingsCategory fittingsCategory) throws Exception {
        boolean modify = fittingsCategoryService.modify(fittingsCategory);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, modify, null);
    }

    @GetMapping("/detail")
    public BaseResponseInfo detail(@RequestParam(name = "id") Long id) throws BusinessParamCheckingException {
        FittingsCategory detail = fittingsCategoryService.detail(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, detail);
    }

    @GetMapping("/queryList")
    public BaseResponseInfo queryList(FittingsCategoryQuery fittingsCategoryQuery) {
        IPage<FittingsCategory> fittingsCategoryIPage = fittingsCategoryService.queryList(fittingsCategoryQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, fittingsCategoryIPage);
    }

    @PostMapping("/remove")
    public BaseResponseInfo remove(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        boolean remove = fittingsCategoryService.remove(carModelQuery.getId());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/batchRemove")
    public BaseResponseInfo batchRemove(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        boolean remove = fittingsCategoryService.batchRemove(carModelQuery.getIds());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/download")
    public void download(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        fittingsCategoryService.downloadData(carModelQuery.getIds());
    }

    @PostMapping("/import")
    public BaseResponseInfo importData(@RequestPart("file") MultipartFile multipartFile) throws BusinessParamCheckingException, IOException {
        boolean result = fittingsCategoryService.importData(multipartFile);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

}
