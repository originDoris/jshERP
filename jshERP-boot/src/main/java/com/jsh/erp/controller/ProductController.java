package com.jsh.erp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.Product;
import com.jsh.erp.datasource.query.ProductQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.product.ProductService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: CarModelController
 * @Description:
 * @date: 2022/12/7 14:46
 */
@RestController
@RequestMapping(value = "/product")
@Api(tags = {"商品"})
@Slf4j
public class ProductController {
    @Resource
    private ProductService productService;

    @PostMapping("/save")
    public BaseResponseInfo save(@RequestBody Product product) throws Exception {
        BaseResponseInfo baseResponseInfo = new BaseResponseInfo();
        boolean result = productService.save(product);
        baseResponseInfo.setData(result);
        baseResponseInfo.setCode(SERVICE_SUCCESS_CODE);
        return baseResponseInfo;
    }


    @PostMapping("/modify")
    public BaseResponseInfo modify(@RequestBody Product product) throws Exception {
        boolean modify = productService.modify(product);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, modify, null);
    }

    @GetMapping("/detail")
    public BaseResponseInfo detail(@RequestParam(name = "id") Long id) throws BusinessParamCheckingException {
        Product detail = productService.detail(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, detail);
    }

    @GetMapping("/queryList")
    public BaseResponseInfo queryList(ProductQuery productQuery) {
        IPage<Product> carModelIPage = productService.queryList(productQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, carModelIPage);
    }

    @GetMapping("/queryWarehousingList")
    public BaseResponseInfo queryWarehousingList(ProductQuery productQuery) {
        productQuery.setStatus("1");
        List<Product> list = productService.queryByStatus(productQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, list);
    }


    @GetMapping("/queryReturnList")
    public BaseResponseInfo queryReturnList(ProductQuery productQuery) {
        productQuery.setStatus("2");
        List<Product> list = productService.queryByStatus(productQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, list);
    }

    @PostMapping("/remove")
    public BaseResponseInfo remove(@RequestBody ProductQuery productQuery) throws BusinessParamCheckingException {
        boolean remove = productService.remove(productQuery.getId());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/batchRemove")
    public BaseResponseInfo batchRemove(@RequestBody ProductQuery productQuery) throws BusinessParamCheckingException {
        boolean remove = productService.batchRemove(productQuery.getIds());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }


    @GetMapping("/generateQR")
    public void generateQR(@RequestParam("code") String code) throws Exception {
        productService.generateQR(code);
    }
}
