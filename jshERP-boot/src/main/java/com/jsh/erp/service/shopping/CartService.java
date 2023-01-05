package com.jsh.erp.service.shopping;

import com.jsh.erp.datasource.entities.Supplier;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.entities.shopping.Cart;
import com.jsh.erp.datasource.entities.shopping.Cart;
import com.jsh.erp.datasource.mappers.AddressMapper;
import com.jsh.erp.datasource.mappers.CartMapper;
import com.jsh.erp.datasource.query.AddressQuery;
import com.jsh.erp.datasource.query.CartQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jsh.erp.constants.ExceptionConstants.*;
import static com.jsh.erp.service.redis.RedisService.ACCESS_TOKEN;

/**
 * @author: origindoris
 * @Title: CarModelService
 * @Description:
 * @date: 2022/12/6 13:45
 */
@Service
@Slf4j
public class CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @Resource
    private SupplierService supplierService;


    public List<Cart> queryList(CartQuery cartQuery) {
        return cartMapper.queryList(cartQuery);
    }

    public Cart detail(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CART_ID_IS_NULL_CODE, CART_ID_IS_NULL_MSG);
        }
        return cartMapper.detail(id);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean save(Cart cart) throws Exception {
        verifyParam(cart);
        Long userId = userService.getUserId(request);
        String token = request.getHeader(ACCESS_TOKEN);
        Long tenantId = Tools.getTenantIdByToken(token);
        cart.setTenantId(tenantId);
        cart.setOperator(userId);
        cart.setCreateTime(new Date());
        cart.setDeleteFlag("0");
        return cartMapper.save(cart);
    }



    public boolean remove(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CART_ID_IS_NULL_CODE, CART_ID_IS_NULL_MSG);
        }
        return cartMapper.delete(id);
    }


    public boolean batchRemove(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(CART_ID_LIST_IS_NULL_CODE, CART_ID_LIST_IS_NULL_MSG);
        }
        return cartMapper.batchDelete(ids);
    }


    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean modify(Cart cart) throws Exception {
        verifyParam(cart);
        if (cart.getId() == null) {
            throw new BusinessParamCheckingException(CART_ID_IS_NULL_CODE, CART_ID_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        String token = request.getHeader(ACCESS_TOKEN);
        Long tenantId = Tools.getTenantIdByToken(token);
        cart.setTenantId(tenantId);
        cart.setOperator(userId);
        cart.setUpdateTime(new Date());
        return cartMapper.modify(cart);
    }


    private void verifyParam(Cart product) throws BusinessParamCheckingException {
        try {
            Assert.notNull(product, "购物车商品参数不能为空！");
            Assert.notNull(product.getMaterialId(), "配件id不能为空！");
            Assert.notNull(product.getCount(), "配件数量不能为空！");
        } catch (Exception e) {
            throw new BusinessParamCheckingException(CART_PARAM_ERROR, e.getMessage());
        }
    }

    public Cart getByMaterialId(Long materialId, Long operator) {
        return cartMapper.queryByMaterialId(materialId, operator);
    }


    public List<Cart> queryByIds(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return cartMapper.queryByIds(ids);
    }
    
}
