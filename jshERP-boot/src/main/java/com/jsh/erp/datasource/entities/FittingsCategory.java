package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: FittingsCategory
 * @Description:
 * @date: 2022/12/7 15:39
 */
@Data
public class FittingsCategory implements Serializable {

    private Long id;

    private Long creator;

    private Long modifier;

    private Date createTime;

    private Date updateTime;

    /**
     * 类别名称
     */
    private String categoryName;

    private String code;
}
