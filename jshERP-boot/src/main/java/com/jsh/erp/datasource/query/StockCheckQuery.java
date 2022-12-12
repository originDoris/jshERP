package com.jsh.erp.datasource.query;

import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelquery
 * @Description:
 * @date: 2022/12/6 10:22
 */
@Data
public class StockCheckQuery extends BaseQuery {

    private String operator;

    private String materialName;

    private String startTime;

    private String endTime;

    private Long id;

    private List<Long> ids;

    private String code;

}
