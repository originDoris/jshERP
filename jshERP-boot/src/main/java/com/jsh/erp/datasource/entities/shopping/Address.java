package com.jsh.erp.datasource.entities.shopping;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: Address
 * @Description:
 * @date: 2022/12/19 09:55
 */
@Data
public class Address implements Serializable {
    private Long id;

    private Long operator;

    private Date createTime;

    private Date updateTime;

    private Long tenantId;

    private String deleteFlag;

    private String address;

    private String phone;

    private String name;

    private Boolean defaultFlag;
}
