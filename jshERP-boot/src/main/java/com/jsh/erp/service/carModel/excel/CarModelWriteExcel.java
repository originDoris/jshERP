package com.jsh.erp.service.carModel.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: origindoris
 * @Title: CarModelWriteExcel
 * @Description:
 * @date: 2022/12/7 14:09
 */
@Data
public class CarModelWriteExcel implements Serializable {
    @ExcelProperty("vin")
    private String carVin;

    @ExcelProperty("车型")
    private String carModel;

    @ExcelProperty("车系")
    private String carSeries;

    @ExcelProperty("发动机")
    private String carEngine;

    @ExcelProperty("品牌")
    private String carBrand;

    @ExcelProperty("车组")
    private String carGroup;

    @ExcelProperty("变速箱")
    private String carGearbox;

    @ExcelProperty("排量")
    private String carCc;

    @ExcelProperty("车型代码")
    private String code;
}
