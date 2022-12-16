package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.query.CarModelQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelMapper
 * @Description:
 * @date: 2022/12/6 10:16
 */
public interface CarModelMapper {
    IPage<CarModel> queryList(IPage<CarModel> iPage,@Param("carModelQuery") CarModelQuery carModelQuery);

    CarModel detail(@Param("id") Long id);

    boolean delete(@Param("id") Long id);

    boolean batchDelete(@Param("ids") List<Long> ids);

    boolean modify(CarModel carModel);

    boolean save(CarModel carModel);

    boolean batchSave(@Param("list") List<CarModel> carModels);

    List<CarModel> queryByIds(@Param("ids") List<Long> ids);


    CarModel detailByCode(@Param("code") String code);


    CarModel queryByCarVin(@Param("vin") String vin);


}
