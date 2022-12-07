package com.jsh.erp.datasource.query;

import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelquery
 * @Description:
 * @date: 2022/12/6 10:22
 */
@Data
public class CarModelQuery extends BaseQuery {
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

    private Long id;

    private List<Long> ids;

}
