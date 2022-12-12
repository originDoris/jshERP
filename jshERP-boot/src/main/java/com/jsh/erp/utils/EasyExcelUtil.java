package com.jsh.erp.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.jsh.erp.service.carModel.excel.ExcelReadListener;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author: origindoris
 * @Title: EasyExcelUtil
 * @Description:
 * @date: 2022/12/7 15:15
 */
public class EasyExcelUtil {

    public static <T> void read(InputStream inputStream, Class<T> head, Consumer<List<T>> consumer) {
        EasyExcel.read(inputStream, head, new ExcelReadListener<T>(consumer)).sheet().doRead();
    }

}
