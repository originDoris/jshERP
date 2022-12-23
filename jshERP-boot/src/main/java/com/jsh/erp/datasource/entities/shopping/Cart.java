package com.jsh.erp.datasource.entities.shopping;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: Cart
 * @Description: 购物车
 * @date: 2022/12/19 13:51
 */
@Data
public class Cart implements Serializable {
    private Long id;

    private Long operator;

    private Date createTime;

    private Date updateTime;

    private Long tenantId;

    private String deleteFlag;

    private Long materialId;

    private Long count;

    private Long cartId;
}
