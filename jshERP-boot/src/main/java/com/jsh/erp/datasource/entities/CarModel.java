package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: carModel
 * @Description:
 * @date: 2022/12/6 10:11
 */
@Data
public class CarModel implements Serializable {
    private Long id;

    private String carVin;

    /**
     * 车型
     */
    private String carModel;

    /**
     * 车系
     */
    private String carSeries;

    /**
     * 发动机
     */
    private String carEngine;

    /**
     * 车辆品牌
     */
    private String carBrand;

    /**
     * 车组
     */
    private String carGroup;

    /**
     * 变速箱
     */
    private String carGearbox;

    /**
     * 排量
     */
    private String carCc;

    private Long creator;

    private Long modifier;

    private Date createTime;

    private Date updateTime;

    private String code;

    private Long tenantId;

    private String deleteFlag;








}
