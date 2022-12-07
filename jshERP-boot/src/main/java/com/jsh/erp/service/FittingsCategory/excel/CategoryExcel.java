package com.jsh.erp.service.FittingsCategory.excel;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: origindoris
 * @Title: CategoryExcel
 * @Description:
 * @date: 2022/12/7 16:31
 */
@Data
public class CategoryExcel implements Serializable {

    private String categoryName;

    private String code;
}
