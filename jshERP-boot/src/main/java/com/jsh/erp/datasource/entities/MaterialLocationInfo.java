package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: origindoris
 * @Title: MaterialLoacionInfo
 * @Description:
 * @date: 2022/12/14 10:25
 */
@Data
public class MaterialLocationInfo implements Serializable {

    private String location;

    private BigDecimal stock;

}
