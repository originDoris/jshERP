package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: OrderInfo
 * @Description:
 * @date: 2022/12/21 14:50
 */
@Data
public class OrderInfo extends DepotHeadVo4List {


    private List<OrderMaterial> orderMaterials;

    private Address addressInfo;

    /**
     * 0 未发货
     * 1 已发货
     */
    private String orderStatus;

    private String orderStatusName;

    private Integer count;
}
