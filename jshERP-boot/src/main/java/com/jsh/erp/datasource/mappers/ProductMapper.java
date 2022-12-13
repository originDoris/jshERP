package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsh.erp.datasource.entities.Product;
import com.jsh.erp.datasource.entities.StockCheck;
import com.jsh.erp.datasource.query.ProductQuery;
import com.jsh.erp.datasource.query.StockCheckQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelMapper
 * @Description:
 * @date: 2022/12/6 10:16
 */
public interface ProductMapper {
    IPage<Product> queryList(IPage<Product> iPage, @Param("query") ProductQuery query);

    Product detail(@Param("id") Long id);

    boolean delete(@Param("id") Long id);

    boolean batchDelete(@Param("ids") List<Long> ids);

    boolean modify(Product product);

    boolean save(Product product);

    List<Product> queryQrCodeIsNotNull(@Param("query") ProductQuery query);
    boolean modifyHeadCode(@Param("codes") List<String> codes, @Param("headCode") String headCode);

    boolean batchSave(@Param("list") List<Product> products);

    List<Product> queryByIds(@Param("ids") List<Long> ids);

    Product detailByCode(@Param("code") String code);

}
