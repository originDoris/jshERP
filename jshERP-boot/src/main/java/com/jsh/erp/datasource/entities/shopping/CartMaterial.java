package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.entities.MaterialVo4Unit;
import lombok.Data;

/**
 * @author: origindoris
 * @Title: CartMaterial
 * @Description:
 * @date: 2022/12/19 14:41
 */
@Data
public class CartMaterial extends MaterialVo4Unit {
    private Long count;

    private Long cartId;
}
