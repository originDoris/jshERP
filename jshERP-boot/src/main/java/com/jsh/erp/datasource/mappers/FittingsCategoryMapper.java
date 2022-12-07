package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.FittingsCategory;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.datasource.query.FittingsCategoryQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: FittingsCategoryMapper
 * @Description:
 * @date: 2022/12/7 15:43
 */
public interface FittingsCategoryMapper {
    IPage<FittingsCategory> queryList(IPage<FittingsCategory> iPage, @Param("querty") FittingsCategoryQuery fittingsCategoryQuery);

    FittingsCategory detail(@Param("id") Long id);

    boolean delete(@Param("id") Long id);

    boolean batchDelete(@Param("ids") List<Long> ids);

    boolean modify(FittingsCategory fittingsCategory);

    boolean save(FittingsCategory fittingsCategory);

    boolean batchSave(@Param("list") List<FittingsCategory> fittingsCategories);

    List<FittingsCategory> queryByIds(@Param("ids") List<Long> ids);
}
