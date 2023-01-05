package com.jsh.erp.annotation;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: ErpPage
 * @Description:
 * @date: 2022/12/28 15:16
 */
@Data
public class ErpPage<T> extends Page<T> {
    private List<T> rows;
}
