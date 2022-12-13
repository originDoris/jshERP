package com.jsh.erp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.carModel.CarModelService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.List;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: CarModelController
 * @Description:
 * @date: 2022/12/7 14:46
 */
@RestController
@RequestMapping(value = "/car/model")
@Api(tags = {"车型"})
@Slf4j
public class CarModelController {
    @Resource
    private CarModelService carModelService;

    @PostMapping("/save")
    public BaseResponseInfo save(@RequestBody CarModel carModel) throws Exception {
        BaseResponseInfo baseResponseInfo = new BaseResponseInfo();
        boolean result = carModelService.save(carModel);
        baseResponseInfo.setData(result);
        baseResponseInfo.setCode(SERVICE_SUCCESS_CODE);
        return baseResponseInfo;
    }


    @PostMapping("/modify")
    public BaseResponseInfo modify(@RequestBody CarModel carModel) throws Exception {
        boolean modify = carModelService.modify(carModel);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, modify, null);
    }

    @GetMapping("/detail")
    public BaseResponseInfo detail(@RequestParam(name = "id") Long id) throws BusinessParamCheckingException {
        CarModel detail = carModelService.detail(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, detail);
    }

    @GetMapping("/queryList")
    public BaseResponseInfo queryList(CarModelQuery carModelQuery) {
        IPage<CarModel> carModelIPage = carModelService.queryList(carModelQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, carModelIPage);
    }

    @PostMapping("/remove")
    public BaseResponseInfo remove(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        boolean remove = carModelService.remove(carModelQuery.getId());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/batchRemove")
    public BaseResponseInfo batchRemove(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        boolean remove = carModelService.batchRemove(carModelQuery.getIds());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/download")
    public void download(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        carModelService.downloadData(carModelQuery.getIds());
    }

    @PostMapping("/import")
    public BaseResponseInfo importData(@RequestPart("file") MultipartFile multipartFile) throws BusinessParamCheckingException, IOException {
        boolean result = carModelService.importData(multipartFile);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

}
