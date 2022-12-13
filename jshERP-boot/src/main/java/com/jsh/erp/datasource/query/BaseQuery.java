package com.jsh.erp.datasource.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: origindoris
 * @Title: BaseQuery
 * @Description:
 * @date: 2022/12/6 10:21
 */
@Data
public class BaseQuery implements Serializable {

    private Long pageSize = 10L;

    private Long pageNum = 1L;

    private Long tenantId;


}
