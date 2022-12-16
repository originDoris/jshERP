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
public class ProductQuery extends BaseQuery {

    private String operator;

    private String materialName;

    private String oem;

    private String categoryName;

    private String carModel;

    private String vin;

    private String depotId;

    private Long id;

    private List<Long> ids;

    private String code;

    private Boolean qrFlag;

    /**
     * 1. 未入库
     * 2. 已入库
     * 3. 已出库
     */
    private String status;


}
