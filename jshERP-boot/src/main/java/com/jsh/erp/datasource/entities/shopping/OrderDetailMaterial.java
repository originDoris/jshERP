package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.entities.CarModel;
import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: OrderDetailMaterial
 * @Description:
 * @date: 2022/12/22 11:34
 */
@Data
public class OrderDetailMaterial extends CarModel {
    private List<OrderMaterial> materials;
}
