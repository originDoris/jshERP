package com.jsh.erp.datasource.query;

import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: AddressQuery
 * @Description:
 * @date: 2022/12/19 09:59
 */
@Data
public class CartQuery {

    private Long operator;

    private List<Long> ids;

}
