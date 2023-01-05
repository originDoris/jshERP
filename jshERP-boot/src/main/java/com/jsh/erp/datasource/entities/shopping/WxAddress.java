package com.jsh.erp.datasource.entities.shopping;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: origindoris
 * @Title: WxAddress
 * @Description:
 * @date: 2023/1/4 13:39
 */
@Data
public class WxAddress implements Serializable {
    private String userName;

    private String telNumber;

    private String provinceName;

    private String cityName;

    private String countyName;

    private String detailInfo;
}
