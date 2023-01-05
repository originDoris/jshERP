package com.jsh.erp.datasource.entities.shopping;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: origindoris
 * @Title: SalesOrder
 * @Description:
 * @date: 2022/12/20 14:14
 */
@Data
public class SalesOrderParam implements Serializable {

    private Long addressId;

    private List<Cart> carts;

    private String code;
}
