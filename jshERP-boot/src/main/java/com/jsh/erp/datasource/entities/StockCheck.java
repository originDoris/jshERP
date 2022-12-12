package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: StockCheck
 * @Description:
 * @date: 2022/12/12 15:04
 */
@Data
public class StockCheck implements Serializable {

    private Long id;

    private Long operator;

    private Date createTime;

    private Date updateTime;

    private String code;

    private Long stockNumber;

    private Long realStockNumber;

    private BigDecimal price;

    /**
     * 差异金额
     */
    private BigDecimal difference;

    private Long depotId;

    private Long materialId;

    private String depotName;

    private String materialName;
}
