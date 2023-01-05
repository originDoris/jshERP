package com.jsh.erp.datasource.entities.shopping;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: origindoris
 * @Title: CartTotal
 * @Description:
 * @date: 2023/1/5 19:12
 */
@Data
public class CartTotal implements Serializable {
    private List<CartDTO> cartDTOS;
    private BigDecimal totalCount;
    private BigDecimal totalPrice;
}
