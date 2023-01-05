package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.Product;
import com.jsh.erp.datasource.entities.shopping.Address;
import com.jsh.erp.datasource.query.AddressQuery;
import com.jsh.erp.datasource.query.ProductQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelMapper
 * @Description:
 * @date: 2022/12/6 10:16
 */
public interface AddressMapper {
    List<Address> queryList( @Param("query") AddressQuery query);

    Address detail(@Param("id") Long id);

    boolean delete(@Param("id") Long id);

    boolean batchDelete(@Param("ids") List<Long> ids);

    boolean modify(Address address);

    boolean save(Address address);

    boolean batchSave(@Param("list") List<Address> addresses);

    List<Address> queryByIds(@Param("ids") List<Long> ids);







}
