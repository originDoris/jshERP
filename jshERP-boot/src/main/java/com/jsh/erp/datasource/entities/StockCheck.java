package com.jsh.erp.datasource.entities;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String code;

    private Long stockNumber;

    private Long realStockNumber;

    private BigDecimal price;

    private String deleteFlag;

    /**
     * 差异金额
     */
    private BigDecimal difference;

    private Long depotId;

    private Long materialId;

    private String depotName;

    private String materialName;

    private Long tenantId;
}
