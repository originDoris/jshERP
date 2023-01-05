package com.jsh.erp.datasource.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: origindoris
 * @Title: HistoryQuery
 * @Description:
 * @date: 2023/1/3 14:11
 */
@Data
public class HistoryQuery implements Serializable {
    private List<String> carModelCodes;
}
