package com.jsh.erp.datasource.entities.shopping;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: origindoris
 * @Title: History
 * @Description:
 * @date: 2023/1/3 13:37
 */
@Data
public class History implements Serializable {
    private Long id;


    private Long operator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Long tenantId;

    private String deleteFlag;

    private String carModelCode;
}
