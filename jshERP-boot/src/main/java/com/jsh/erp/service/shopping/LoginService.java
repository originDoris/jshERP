package com.jsh.erp.service.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.Supplier;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: LoginService
 * @Description:
 * @date: 2022/12/21 10:31
 */
@Service
@Slf4j
public class LoginService {

    @Value("${wx.appid}")
    private String appId;

    @Value("${wx.appSecret}")
    private String appSecret;

    @Value("${default.tenant.id}")
    private String defaultTenantId;

    @Resource
    private UserService userService;

    @Resource
    private SupplierService supplierService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private RedisService redisService;


    public static final String LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    public static final String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";

    public static final String GET_PHONE = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";

    public BaseResponseInfo login(String code) throws Exception {
        String url = LOGIN + "?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        JSONObject jsonObject = HttpClient.httpGet(url);
        log.info("wxlogin.result:{}", jsonObject);
        String openid = jsonObject.getString("openid");
        if (openid == null) {
            throw new BusinessRunTimeException(WX_LOGIN_FAIL_CODE, WX_LOGIN_FAIL_MSG);
        }
        Supplier supplier = supplierService.queryByOpenId(openid);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setOpenId(openid);
            supplier.setWxFlag(true);
            supplier.setSupplier("wx_" + openid);
            supplier.setType("客户");
            supplier.setTenantId(Long.parseLong(defaultTenantId));
            supplierService.insertSupplier(JSON.parseObject(JSON.toJSONString(supplier)), request);
            supplier = supplierService.queryByOpenId(openid);
        }
        Long id = supplier.getId();
        Object openId = redisService.getObjectFromSessionByKey(request,"openId");
        if (openId != null) {
            log.info("====用户已经登录过, login 方法调用结束====");
        }
        String token = "wx@" + UUID.randomUUID().toString().replaceAll("-", "") + "_" + defaultTenantId;
        redisService.storageObjectBySession(token, "openId", openid);
        redisService.storageObjectBySession(token, "userId", id);
        Map<String, Object> data = new HashMap<String, Object>(1);
        data.put("token", token);
        data.put("user", supplier);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, data);
    }

    public String getAccessToken(){
        String url = GET_ACCESS_TOKEN + "?appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential";
        JSONObject jsonObject = HttpClient.httpGet(url);
        log.info("wx.getAccessToken.result:{}", jsonObject);
        String accessToken = jsonObject.getString("access_token");
        if (accessToken == null) {
            throw new BusinessRunTimeException(WX_GET_ACCESS_TOKEN_FAIL_CODE, WX_GET_ACCESS_TOKEN_FAIL_MSG);
        }
        return accessToken;
    }

    public String getUserPhone(String phoneCode){
        String accessToken = getAccessToken();
        String url = GET_PHONE + "?access_token=" + accessToken;
        HashMap<String, Object> param = new HashMap<>();
        param.put("code", phoneCode);
        String httpPost = HttpClient.httpPost(url, JSON.toJSONString(param));
        log.info("wx.getUserPhone.result:{}", httpPost);
        JSONObject result = JSON.parseObject(httpPost);
        JSONObject info = result.getJSONObject("phone_info");
        if (info == null) {
            throw new BusinessRunTimeException(WX_GET_PHONE_FAIL_CODE, WX_GET_PHONE_FAIL_MSG);
        }
        String phoneNumber = info.getString("phoneNumber");
        if (phoneNumber == null) {
            throw new BusinessRunTimeException(WX_GET_PHONE_FAIL_CODE, WX_GET_PHONE_FAIL_MSG);
        }
        return phoneNumber;
    }


    public boolean setUserPhone(String phoneCode) throws Exception {
        Long userId = userService.getUserId(request);
        Supplier supplier = supplierService.getSupplier(userId);
        if (supplier == null) {
            throw new BusinessRunTimeException(WX_CUSTOMER_NON_EXISTENT_CODE, WX_CUSTOMER_NON_EXISTENT_MSG);
        }
        String userPhone = getUserPhone(phoneCode);
        supplier.setPhoneNum(userPhone);
        int i = supplierService.updateSupplier(JSON.parseObject(JSON.toJSONString(supplier)), request);
        return i > 0;
    }

}
