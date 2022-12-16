package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.MaterialCategory;
import lombok.Data;
import org.apache.log4j.Category;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelCategory
 * @Description:
 * @date: 2022/12/15 13:52
 */
@Data
public class CarModelCategory extends CarModel {

    private List<ShoppingCategory> categories;
}
