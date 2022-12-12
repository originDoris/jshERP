package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.StockCheck;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.datasource.query.StockCheckQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelMapper
 * @Description:
 * @date: 2022/12/6 10:16
 */
public interface StockCheckMapper {
    IPage<StockCheck> queryList(IPage<StockCheck> iPage, @Param("query") StockCheckQuery query);

    StockCheck detail(@Param("id") Long id);

    boolean delete(@Param("id") Long id);

    boolean batchDelete(@Param("ids") List<Long> ids);

    boolean modify(StockCheck stockCheck);

    boolean save(StockCheck stockCheck);

    boolean batchSave(@Param("list") List<StockCheck> stockChecks);

    List<StockCheck> queryByIds(@Param("ids") List<Long> ids);

}
