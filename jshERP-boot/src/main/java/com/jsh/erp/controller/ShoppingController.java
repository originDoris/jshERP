package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.shopping.CarModelCategory;
import com.jsh.erp.datasource.query.ShoppingQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.shopping.ShoppingService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.jsh.erp.constants.ExceptionConstants.SERVICE_SUCCESS_CODE;

/**
 * @author: origindoris
 * @Title: ShoppingController
 * @Description:
 * @date: 2022/12/15 16:37
 */
@RestController
@RequestMapping(value = "/shopping")
@Slf4j
public class ShoppingController {

    @Resource
    private ShoppingService shoppingService;

    @GetMapping("/query")
    public BaseResponseInfo query(ShoppingQuery shoppingQuery) throws BusinessParamCheckingException {
        CarModelCategory carModelCategory = shoppingService.queryCommodityList(shoppingQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, carModelCategory);
    }

}
