package com.jsh.erp.service.shopping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.datasource.entities.shopping.Address;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.AddressMapper;
import com.jsh.erp.datasource.mappers.ProductMapper;
import com.jsh.erp.datasource.query.AddressQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.QRUtil;
import com.jsh.erp.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
public class AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;


    public List<Address> queryList(AddressQuery addressQuery) throws Exception {
        Long userId = userService.getUserId(request);
        addressQuery.setOperator(userId);
        return addressMapper.queryList(addressQuery);
    }

    public Address detail(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(ADDRESS_ID_IS_NULL_CODE, ADDRESS_ID_IS_NULL_MSG);
        }
        return addressMapper.detail(id);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean save(Address address) throws Exception {
        verifyParam(address);
        Long userId = userService.getUserId(request);
        String token = request.getHeader(ACCESS_TOKEN);
        Long tenantId = Tools.getTenantIdByToken(token);
        address.setTenantId(tenantId);
        address.setOperator(userId);
        address.setCreateTime(new Date());
        address.setDeleteFlag("0");
        if (address.getDefaultFlag() != null && address.getDefaultFlag()) {
            AddressQuery addressQuery = new AddressQuery();
            addressQuery.setOperator(userId);
            addressQuery.setDefaultFlag(true);
            List<Address> addresses = queryList(addressQuery);
            if (addresses != null && !addresses.isEmpty()) {
                for (Address address1 : addresses) {
                    address1.setDefaultFlag(false);
                    modify(address1);
                }
            }
            address.setDefaultFlag(address.getDefaultFlag());
        }else{
            address.setDefaultFlag(false);
        }
        return addressMapper.save(address);
    }




    public boolean setDefault(Long id) throws Exception {
        Long userId = userService.getUserId(request);
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.setOperator(userId);
        List<Address> addresses = queryList(addressQuery);
        if (addresses == null || addresses.isEmpty()) {
            throw new BusinessParamCheckingException(ADDRESS_IS_NULL_CODE, ADDRESS_IS_NULL_MSG);
        }
        for (Address address : addresses) {
            if (address.getId().equals(id)) {
                address.setDefaultFlag(true);
                modify(address);
                continue;
            }
            if (address.getDefaultFlag() != null && address.getDefaultFlag()) {
                address.setDefaultFlag(false);
                modify(address);
            }
        }
        return true;
    }
    


    public boolean remove(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(ADDRESS_ID_IS_NULL_CODE, ADDRESS_ID_IS_NULL_MSG);
        }
        return addressMapper.delete(id);
    }


    public boolean batchRemove(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(ADDRESS_ID_LIST_IS_NULL_CODE, ADDRESS_ID_LIST_IS_NULL_MSG);
        }
        return addressMapper.batchDelete(ids);
    }


    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean modify(Address address) throws Exception {
        verifyParam(address);
        if (address.getId() == null) {
            throw new BusinessParamCheckingException(ADDRESS_ID_IS_NULL_CODE, ADDRESS_ID_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        String token = request.getHeader(ACCESS_TOKEN);
        Long tenantId = Tools.getTenantIdByToken(token);
        address.setTenantId(tenantId);
        address.setOperator(userId);
        address.setUpdateTime(new Date());
        if (address.getDefaultFlag() != null && address.getDefaultFlag()) {
            AddressQuery addressQuery = new AddressQuery();
            addressQuery.setOperator(userId);
            addressQuery.setDefaultFlag(true);
            List<Address> addresses = queryList(addressQuery);
            if (addresses != null && !addresses.isEmpty()) {
                for (Address address1 : addresses) {
                    address1.setDefaultFlag(false);
                    addressMapper.modify(address1);
                }
            }
            address.setDefaultFlag(address.getDefaultFlag());
        }
        return addressMapper.modify(address);
    }


    private void verifyParam(Address product) throws BusinessParamCheckingException {
        try {
            Assert.notNull(product, "地址参数不能为空！");
            Assert.notNull(product.getAddress(), "收货地址不能为空！");
            Assert.notNull(product.getName(), "收货人名称不能为空！");
            Assert.notNull(product.getPhone(), "收货人电话不能为空！");
        } catch (Exception e) {
            throw new BusinessParamCheckingException(ADDRESS_PARAM_ERROR, e.getMessage());
        }
    }

    public List<Address> queryByIds(List<Long> ids) {
       return addressMapper.queryByIds(ids);
    }


    public boolean batchSave(List<Address> addresses){
        if (addresses == null || addresses.isEmpty()) {
            return true;
        }
        return addressMapper.batchSave(addresses);
    }
    
}
