package com.jsh.erp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.StockCheck;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.datasource.query.StockCheckQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.stockCheck.StockCheckService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(value = "/inventory")
@Api(tags = {"盘点"})
@Slf4j
public class StockCheckController {
    @Resource
    private StockCheckService stockCheckService;

    @PostMapping("/save")
    public BaseResponseInfo save(@RequestBody StockCheck stockCheck) throws Exception {
        BaseResponseInfo baseResponseInfo = new BaseResponseInfo();
        boolean result = stockCheckService.save(stockCheck);
        baseResponseInfo.setData(result);
        baseResponseInfo.setCode(SERVICE_SUCCESS_CODE);
        return baseResponseInfo;
    }


    @PostMapping("/modify")
    public BaseResponseInfo modify(@RequestBody StockCheck stockCheck) throws Exception {

        boolean modify = stockCheckService.modify(stockCheck);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, modify, null);

    }

    @GetMapping("/detail")
    public BaseResponseInfo detail(@RequestParam(name = "id") Long id) throws BusinessParamCheckingException {
        StockCheck detail = stockCheckService.detail(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, detail);
    }

    @GetMapping("/queryList")
    public BaseResponseInfo queryList(StockCheckQuery query) {
        IPage<StockCheck> stockCheckIPage = stockCheckService.queryList(query);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, stockCheckIPage);
    }

    @PostMapping("/remove")
    public BaseResponseInfo remove(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        boolean remove = stockCheckService.remove(carModelQuery.getId());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/batchRemove")
    public BaseResponseInfo batchRemove(@RequestBody CarModelQuery carModelQuery) throws BusinessParamCheckingException {
        boolean remove = stockCheckService.batchRemove(carModelQuery.getIds());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }
}
