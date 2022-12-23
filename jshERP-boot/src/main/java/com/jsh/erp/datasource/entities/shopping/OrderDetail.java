package com.jsh.erp.datasource.entities.shopping;

import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import lombok.Data;

import java.util.List;

/**
 * @author: origindoris
 * @Title: OrderDetail
 * @Description:
 * @date: 2022/12/22 11:33
 */
@Data
public class OrderDetail extends DepotHeadVo4List {

    private List<OrderDetailMaterial> orderDetail;

    private Address addressInfo;
}
