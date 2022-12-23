package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.entities.CarModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: origindoris
 * @Title: CartDTO
 * @Description:
 * @date: 2022/12/19 14:33
 */
@Data
public class CartDTO extends CarModel implements Serializable {

    private List<CartMaterial> materials;

}

