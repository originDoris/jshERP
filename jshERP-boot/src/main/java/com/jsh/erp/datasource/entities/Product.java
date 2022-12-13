package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: Product
 * @Description:
 * @date: 2022/12/12 18:10
 */
@Data
public class Product implements Serializable {
    private Long id;

    private Long operator;

    private Date createTime;

    private Date updateTime;

    private String code;

    private Long tenantId;

    private String deleteFlag;

    private String productName;

    private String color;

    private Long depotId;

    private String location;

    private String remark;

    private Long materialId;

    private String carModelCode;

    private String depotName;

    private String materialName;

    private String oem;

    private String vin;

    private String carModel;

    private String categoryName;

    private String headCode;

    private boolean qrFlag;

}
