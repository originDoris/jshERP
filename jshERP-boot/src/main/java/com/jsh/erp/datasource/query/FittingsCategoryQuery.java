package com.jsh.erp.datasource.query;

import lombok.Data;


/**
 * @author: origindoris
 * @Title: FittingsCategoryQuery
 * @Description:
 * @date: 2022/12/7 15:50
 */
@Data
public class FittingsCategoryQuery extends BaseQuery {
    private String code;

    private String categoryName;

}
