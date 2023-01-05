package com.jsh.erp.service.baidubce;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.utils.Base64Util;
import com.jsh.erp.utils.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author: origindoris
 * @Title: BaidubceService
 * @Description:
 * @date: 2022/12/26 14:01
 */
@Service
@Slf4j
public class BaidubceService {

    @Value("${ocr.api.key}")
    private String apiKey;

    @Value("${ocr.api.secret}")
    private String apiSecret;


    public String getAuth() throws IOException {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + apiKey
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + apiSecret;

        URL realUrl = new URL(getAccessTokenUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line);
        }
        JSONObject jsonObject = JSON.parseObject(result.toString());
        return jsonObject.getString("access_token");
    }


    public JSONObject accurateBasic(byte[] imgData) throws IOException {
        String imgStr = Base64Util.encode(imgData);
        String imgParam = URLEncoder.encode(imgStr, "UTF-8");

        String accessToken = getAuth();
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + accessToken;
        String param = "image=" + imgParam;
        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String result = HttpClient.httpPost(url, param);
        return JSON.parseObject(result);
    }

//
//    public static void main(String[] args) throws IOException {
//
//        byte[] bFile = Files.readAllBytes(new File("/Users/origindoris/Documents/20221226110309.jpg").toPath());
//
//        accurateBasic(bFile);
//    }

}
