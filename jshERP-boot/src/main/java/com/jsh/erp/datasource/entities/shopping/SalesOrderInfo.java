package com.jsh.erp.datasource.entities.shopping;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: origindoris
 * @Title: SalesOrderInfo
 * @Description:
 * @date: 2023/1/5 18:37
 */
@Data
public class SalesOrderInfo implements Serializable {

    private String code;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    private BigDecimal totalPrice;

    private Integer totalCount;

    private List<CartDTO> carts;
}
