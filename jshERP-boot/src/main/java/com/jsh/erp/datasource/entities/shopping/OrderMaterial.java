package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.DepotItem;
import com.jsh.erp.datasource.entities.DepotItemVo4WithInfoEx;
import lombok.Data;

/**
 * @author: origindoris
 * @Title: OrderMaterial
 * @Description:
 * @date: 2022/12/21 15:02
 */
@Data
public class OrderMaterial extends DepotItemVo4WithInfoEx {

    private CarModel carModel;
}
