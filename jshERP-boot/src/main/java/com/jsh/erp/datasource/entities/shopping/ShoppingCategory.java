package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.entities.Material;
import com.jsh.erp.datasource.entities.MaterialCategory;
import com.jsh.erp.datasource.entities.MaterialVo4Unit;
import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: ShoppingCategory
 * @Description:
 * @date: 2022/12/15 14:24
 */
@Data
public class ShoppingCategory extends MaterialCategory {

    private List<MaterialVo4Unit> materials;
}
