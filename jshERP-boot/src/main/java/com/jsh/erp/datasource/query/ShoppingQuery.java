package com.jsh.erp.datasource.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: origindoris
 * @Title: ShoppingQuery
 * @Description:
 * @date: 2022/12/15 14:38
 */
@Data
public class ShoppingQuery implements Serializable {

    private String name;

    private String vin;

    private String carModelCode;
}
