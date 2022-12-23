package com.jsh.erp.service.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.Supplier;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.supplier.SupplierService;
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

import static com.jsh.erp.constants.ExceptionConstants.SERVICE_SUCCESS_CODE;

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

    @Value("${default.tenant.id}")
    private String defaultTenantId;

    @Resource
    private SupplierService supplierService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private RedisService redisService;

    public static final String LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    public BaseResponseInfo login(String code) throws Exception {
        String url = LOGIN + "?appid=" + appId + "&js_code=" + code + "&grant_type=authorization_code";
//        JSONObject jsonObject = HttpClient.httpGet(url);
//        String openid = jsonObject.getString("openid");
        String openid = code;

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
}
