package com.jsh.erp.datasource.entities.shopping;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Long tenantId;

    private String deleteFlag;

    private String address;

    private String phone;

    private String name;

    private Boolean defaultFlag;
}
