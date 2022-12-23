package com.jsh.erp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.shopping.Address;
import com.jsh.erp.datasource.query.AddressQuery;
import com.jsh.erp.datasource.query.ProductQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.shopping.AddressService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.jsh.erp.constants.ExceptionConstants.SERVICE_SUCCESS_CODE;

/**
 * @author: origindoris
 * @Title: CarModelController
 * @Description:
 * @date: 2022/12/7 14:46
 */
@RestController
@RequestMapping(value = "/address")
@Api(tags = {"收货地址"})
@Slf4j
public class AddressController {
    @Resource
    private AddressService addressService;

    @PostMapping("/save")
    public BaseResponseInfo save(@RequestBody Address address) throws Exception {
        BaseResponseInfo baseResponseInfo = new BaseResponseInfo();
        boolean result = addressService.save(address);
        baseResponseInfo.setData(result);
        baseResponseInfo.setCode(SERVICE_SUCCESS_CODE);
        return baseResponseInfo;
    }


    @PostMapping("/modify")
    public BaseResponseInfo modify(@RequestBody Address address) throws Exception {
        boolean modify = addressService.modify(address);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, modify, null);
    }

    @GetMapping("/detail")
    public BaseResponseInfo detail(@RequestParam(name = "id") Long id) throws BusinessParamCheckingException {
        Address detail = addressService.detail(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, detail);
    }

    @GetMapping("/queryList")
    public BaseResponseInfo queryList(AddressQuery addressQuery) {
        List<Address> result = addressService.queryList(addressQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }



    @PostMapping("/remove")
    public BaseResponseInfo remove(@RequestBody AddressQuery addressQuery) throws BusinessParamCheckingException {
        boolean remove = addressService.remove(addressQuery.getId());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }

    @PostMapping("/batchRemove")
    public BaseResponseInfo batchRemove(@RequestBody AddressQuery addressQuery) throws BusinessParamCheckingException {
        boolean remove = addressService.batchRemove(addressQuery.getIds());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, remove);
    }


    @GetMapping("/setDefault")
    public BaseResponseInfo setDefault(@RequestParam(value = "id") Long id) throws Exception {
        boolean result = addressService.setDefault(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }
}
