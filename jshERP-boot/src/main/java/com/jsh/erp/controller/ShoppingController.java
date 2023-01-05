package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.Product;
import com.jsh.erp.datasource.entities.shopping.*;
import com.jsh.erp.datasource.query.HistoryQuery;
import com.jsh.erp.datasource.query.ShoppingQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.baidubce.BaidubceService;
import com.jsh.erp.service.shopping.HistoryService;
import com.jsh.erp.service.shopping.LoginService;
import com.jsh.erp.service.shopping.ShoppingService;
import com.jsh.erp.utils.BaseResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;

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

    @Resource
    private LoginService loginService;

    @Resource
    private BaidubceService baidubceService;

    @Resource
    private HistoryService historyService;

    @GetMapping("/query")
    public BaseResponseInfo query(ShoppingQuery shoppingQuery) throws Exception {
        CarModelCategory carModelCategory = shoppingService.queryCommodityList(shoppingQuery);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, carModelCategory);
    }


    @GetMapping("/stockOut")
    public BaseResponseInfo stockOut(@RequestParam("code") String code) throws Exception {
        boolean result = shoppingService.stockOut(code);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

    @GetMapping("/product/detail")
    public BaseResponseInfo productDetail(@RequestParam("code") String code) throws BusinessParamCheckingException {
        Product product = shoppingService.productDetail(code);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, product);
    }

    @PostMapping("/warehousing")
    public BaseResponseInfo warehousing(@RequestBody Product product) throws Exception {
        boolean warehousing = shoppingService.warehousing(product);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, warehousing);
    }

    @GetMapping("/detailByVin")
    public BaseResponseInfo detailByVin(@RequestParam("vin") String vin) throws Exception {
        CarModel carModel = shoppingService.detailByVin(vin);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, carModel);
    }


    @PostMapping("/cart/add")
    public BaseResponseInfo addCart(@RequestBody Cart cart) throws Exception {
        boolean result = shoppingService.addCart(cart);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

    @PostMapping("/cart/setCount")
    public BaseResponseInfo setCount(@RequestBody Cart cart) throws Exception {
        boolean result = shoppingService.setCount(cart);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

    @GetMapping("/cart/remove")
    public BaseResponseInfo removeCart(@RequestParam("id") Long id) throws BusinessParamCheckingException{
        boolean result = shoppingService.removeCart(id);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

    @GetMapping("/cart/query")
    public BaseResponseInfo queryCart() throws Exception {
        List<CartDTO> result = shoppingService.queryCart();
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }


    @GetMapping("/order/unshippedCount")
    public BaseResponseInfo unshippedCount() throws Exception {
        Long count = shoppingService.getCount(true);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, count);
    }


    @GetMapping("/order/shippedCount")
    public BaseResponseInfo shippedCount() throws Exception {
        Long count = shoppingService.getCount(false);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, count);
    }

    @GetMapping("/order/queryOrderDetail")
    public BaseResponseInfo queryOrderDetail(@RequestParam("headId") Long headId) throws Exception {
        OrderDetail orderDetail = shoppingService.queryOrderDetail(headId);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, orderDetail);
    }

    @GetMapping("/order/queryAll")
    public BaseResponseInfo queryAllOrderList() throws Exception {
        List<OrderInfo> orderInfos = shoppingService.queryOrderList(null);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, orderInfos);
    }


    @PostMapping("/order/preOrder")
    private BaseResponseInfo preOrder(@RequestBody SalesOrderParam order) throws Exception {
        SalesOrderInfo salesOrder = shoppingService.getSalesOrder(order);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, salesOrder);
    }

    @GetMapping("/order/queryUnshipped")
    public BaseResponseInfo queryUnshipped() throws Exception {
        List<OrderInfo> orderInfos = shoppingService.queryOrderList(true);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, orderInfos);
    }

    @GetMapping("/order/queryShipped")
    public BaseResponseInfo queryShipped() throws Exception {
        List<OrderInfo> orderInfos = shoppingService.queryOrderList(false);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, orderInfos);
    }


    @PostMapping("/order/generateSalesOrder")
    public BaseResponseInfo generateSalesOrder(@RequestBody SalesOrderParam order) throws Exception {
        boolean result = shoppingService.generateSalesOrder(order);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

    @GetMapping("/wx/login")
    public BaseResponseInfo login(@RequestParam("code") String code) throws Exception {
        return loginService.login(code);
    }

//    @PostMapping("/ocr")
//    public BaseResponseInfo ocr(@RequestPart("file") MultipartFile file) throws IOException {
//        JSONObject ocrData = baidubceService.accurateBasic(file.getBytes());
//        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, ocrData);
//    }

    @GetMapping("/history/getList")
    public BaseResponseInfo getHistoryList() throws Exception {
        List<HistoryInfo> historyInfos = historyService.queryList();
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, historyInfos);
    }

    @PostMapping("/history/remove")
    public BaseResponseInfo removeByIds(@RequestBody HistoryQuery historyQuery) throws Exception {
        boolean result = historyService.removeByCodes(historyQuery.getCarModelCodes());
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }


    @PostMapping("/history/clear")
    public BaseResponseInfo clear() throws Exception {
        boolean result = historyService.removeByUserId();
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }


    @GetMapping("/setUserPhone")
    public BaseResponseInfo setUserPhone(@RequestParam("phoneCode") String phoneCode) throws Exception {
        boolean b = loginService.setUserPhone(phoneCode);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, b);
    }


    @PostMapping("/importAddress")
    public BaseResponseInfo importAddress(@RequestBody List<WxAddress> wxAddresses) throws Exception {
        boolean result = shoppingService.importAddress(wxAddresses);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, result);
    }

}
