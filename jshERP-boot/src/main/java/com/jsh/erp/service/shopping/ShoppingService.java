package com.jsh.erp.service.shopping;

import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.entities.shopping.CarModelCategory;
import com.jsh.erp.datasource.entities.shopping.ShoppingCategory;
import com.jsh.erp.datasource.mappers.CarModelMapper;
import com.jsh.erp.datasource.mappers.MaterialCategoryMapper;
import com.jsh.erp.datasource.mappers.MaterialMapper;
import com.jsh.erp.datasource.mappers.MaterialMapperEx;
import com.jsh.erp.datasource.query.ShoppingQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.carModel.CarModelService;
import com.jsh.erp.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jsh.erp.constants.ExceptionConstants.*;
import static com.jsh.erp.constants.ExceptionConstants.PRODUCT_CODE_IS_NULL_MSG;

/**
 * @author: origindoris
 * @Title: ShoppingService
 * @Description:
 * @date: 2022/12/15 14:31
 */
@Service
@Slf4j
public class ShoppingService {

    @Resource
    private CarModelService carModelService;

    @Resource
    private MaterialMapperEx materialMapperEx;

    @Resource
    private MaterialCategoryMapper materialCategoryMapper;

    @Resource
    private ProductService productService;


    public CarModelCategory queryCommodityList(ShoppingQuery shoppingQuery) throws BusinessParamCheckingException {
        CarModelCategory result = new CarModelCategory();
        CarModel carModel = carModelService.detailCode(shoppingQuery.getCarModelCode());
        if (carModel == null) {
            throw new BusinessParamCheckingException(CAR_MODEL_IS_NULL_CODE, CAR_MODEL_IS_NULL_MSG);
        }
        BeanUtils.copyProperties(carModel, result);
        result.setCategories(new ArrayList<>());
        List<MaterialVo4Unit> materialVo4Units = materialMapperEx.selectByConditionMaterial(shoppingQuery.getName(), null, null, null, null, null, null, null, null, null, null, null, null, null, shoppingQuery.getCarModelCode());
        if (materialVo4Units == null || materialVo4Units.isEmpty()) {
            return result;
        }
        List<Long> categoryIds = materialVo4Units.stream().map(Material::getCategoryId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return result;
        }
        List<MaterialCategory> categories = materialCategoryMapper.queryByIds(categoryIds);
        if (categories == null || categories.isEmpty()) {
            return result;
        }
        ArrayList<ShoppingCategory> shoppingCategories = new ArrayList<>();


        Map<Long, MaterialCategory> categoryMap = categories.stream().collect(Collectors.toMap(MaterialCategory::getId, v -> v, (k1, k2) -> k1));
        Map<Long, List<MaterialVo4Unit>> materialMap = materialVo4Units.stream().collect(Collectors.groupingBy(MaterialVo4Unit::getCategoryId));

        for (Map.Entry<Long, List<MaterialVo4Unit>> entry : materialMap.entrySet()) {
            Long key = entry.getKey();
            MaterialCategory materialCategory = categoryMap.get(key);
            if (materialCategory == null) {
                continue;
            }
            List<MaterialVo4Unit> value = entry.getValue();
            ShoppingCategory shoppingCategory = new ShoppingCategory();
            BeanUtils.copyProperties(materialCategory, shoppingCategory);
            shoppingCategory.setMaterials(value);
            shoppingCategories.add(shoppingCategory);
        }
        result.setCategories(shoppingCategories);
        return result;
    }


    /**
     * 出库
     * @param code 商品代码
     * @return
     * @throws Exception
     */
    public boolean stockOut(String code) throws Exception {
        Product detail = productService.detail(code);
        if (detail == null) {
            throw new BusinessParamCheckingException(PRODUCT_NON_EXIST_CODE, PRODUCT_NON_EXIST_MSG);
        }
        String status = detail.getStatus();
        if (!"2".equals(status)) {
            throw new BusinessParamCheckingException(PRODUCT_STATUS_INCORRECT_CODE, PRODUCT_STATUS_INCORRECT_MSG);
        }
        detail.setStatus("3");
        return productService.modify(detail);
    }


    public Product productDetail(String code) throws BusinessParamCheckingException {
        return productService.detail(code);
    }

    public boolean productAdd(Product product) throws Exception {
        return productService.save(product);
    }

    public CarModel detailByVin(String vin) throws BusinessParamCheckingException {
        return carModelService.detailVin(vin);
    }

}
