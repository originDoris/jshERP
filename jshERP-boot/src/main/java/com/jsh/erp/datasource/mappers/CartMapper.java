package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.shopping.Address;
import com.jsh.erp.datasource.entities.shopping.Cart;
import com.jsh.erp.datasource.query.AddressQuery;
import com.jsh.erp.datasource.query.CartQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelMapper
 * @Description:
 * @date: 2022/12/6 10:16
 */
public interface CartMapper {
    List<Cart> queryList(@Param("query") CartQuery query);

    Cart detail(@Param("id") Long id);

    boolean delete(@Param("id") Long id);

    boolean batchDelete(@Param("ids") List<Long> ids);

    boolean modify(Cart cart);

    boolean save(Cart cart);

    List<Cart> queryByIds(@Param("ids") List<Long> ids);

    Cart queryByMaterialId(@Param("materialId") Long materialId, @Param("operator") Long operator);







}
