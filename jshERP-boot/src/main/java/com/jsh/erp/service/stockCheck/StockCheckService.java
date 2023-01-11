package com.jsh.erp.service.stockCheck;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.annotation.PageConversion;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.entities.StockCheck;
import com.jsh.erp.datasource.mappers.*;
import com.jsh.erp.datasource.query.StockCheckQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.carModel.excel.CarModelReadExcel;
import com.jsh.erp.service.carModel.excel.CarModelWriteExcel;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.materialExtend.MaterialExtendService;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: CarModelService
 * @Description:
 * @date: 2022/12/6 13:45
 */
@Service
@Slf4j
public class StockCheckService {

    @Resource
    private StockCheckMapper stockCheckMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletResponse response;

    @Resource
    private SequenceService sequenceService;

    @Resource
    private MaterialMapperEx materialMapperEx;



    public static final String CODE_PREFIX = "PD";



    @PageConversion
    public IPage<StockCheck> queryList(StockCheckQuery stockCheckQuery) {
        Page<StockCheck> stockCheckPage = new Page<>(stockCheckQuery.getPageNum(), stockCheckQuery.getPageSize());
        return stockCheckMapper.queryList(stockCheckPage, stockCheckQuery);
    }

    public StockCheck detail(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CHECK_ID_IS_NULL_CODE, CHECK_ID_IS_NULL_MSG);
        }
        return stockCheckMapper.detail(id);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean save(StockCheck stockCheck) throws Exception {
        verifyParam(stockCheck);
        Long userId = userService.getUserId(request);
        User user = userService.getUser(userId);
        stockCheck.setTenantId(user.getTenantId());
        stockCheck.setOperator(userId);
        stockCheck.setCreateTime(new Date());
        stockCheck.setDeleteFlag("0");
        inventory(stockCheck);
        if (StringUtils.isBlank(stockCheck.getCode())) {
            String code = sequenceService.buildOnlyNumber(CODE_PREFIX);
            stockCheck.setCode(code);
        }
        return stockCheckMapper.save(stockCheck);
    }

    private void inventory(StockCheck stockCheck) throws BusinessParamCheckingException {
        MaterialVo4Unit materialVo4Unit = materialMapperEx.queryByBarCode(stockCheck.getBarCode());
        if (materialVo4Unit == null) {
            throw new BusinessParamCheckingException(CHECK_MATERIAL_IS_NULL_CODE, CHECK_MATERIAL_IS_NULL_MSG);
        }
        stockCheck.setMaterialId(materialVo4Unit.getId());
        BigDecimal price = stockCheck.getPrice();
        Long stockNumber = stockCheck.getStockNumber();
        Long realStockNumber = stockCheck.getRealStockNumber();
        BigDecimal stockCount = new BigDecimal(stockNumber);
        BigDecimal realStockCount = new BigDecimal(realStockNumber);
        BigDecimal stockAll = stockCount.multiply(price);
        BigDecimal realStockAll = realStockCount.multiply(price);
        if (stockAll.compareTo(realStockAll) == 0) {
            stockCheck.setDifference(BigDecimal.ZERO);
        }else{
            stockCheck.setDifference(stockAll.subtract(realStockAll));
        }
    }


    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean batchSave(List<StockCheck> carModels) throws Exception {
        if (carModels == null || carModels.isEmpty()) {
            throw new BusinessParamCheckingException(CHECK_ID_LIST_IS_NULL_CODE, CHECK_ID_LIST_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        User user = userService.getUser(userId);
        for (StockCheck stockCheck : carModels) {
            verifyParam(stockCheck);
            stockCheck.setTenantId(user.getTenantId());
            stockCheck.setOperator(userId);
            stockCheck.setCreateTime(new Date());
            if (StringUtils.isBlank(stockCheck.getCode())) {
                String code = sequenceService.buildOnlyNumber(CODE_PREFIX);
                stockCheck.setCode(code);
            }
            stockCheck.setDeleteFlag("0");
            inventory(stockCheck);
        }
        return stockCheckMapper.batchSave(carModels);
    }

    public boolean remove(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CHECK_ID_IS_NULL_CODE, CHECK_ID_IS_NULL_MSG);
        }
        return stockCheckMapper.delete(id);
    }


    public boolean batchRemove(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(CHECK_ID_LIST_IS_NULL_CODE, CHECK_ID_LIST_IS_NULL_MSG);
        }
        return stockCheckMapper.batchDelete(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean modify(StockCheck stockCheck) throws Exception {
        verifyParam(stockCheck);
        if (stockCheck.getId() == null) {
            throw new BusinessParamCheckingException(CHECK_ID_IS_NULL_CODE, CHECK_ID_IS_NULL_MSG);
        }
        inventory(stockCheck);
        Long userId = userService.getUserId(request);
        User user = userService.getUser(userId);
        stockCheck.setTenantId(user.getTenantId());
        stockCheck.setOperator(userId);
        stockCheck.setUpdateTime(new Date());
        return stockCheckMapper.modify(stockCheck);
    }

    private void verifyParam(StockCheck stockCheck) throws BusinessParamCheckingException {
        try {
            Assert.notNull(stockCheck, "盘点参数不能为空！");
            Assert.notNull(stockCheck.getDepotId(), "盘点仓库不能为空！");
            Assert.notNull(stockCheck.getMaterialId(),"盘点配件不能为空！");
            Assert.notNull(stockCheck.getRealStockNumber(),"实存数量不能为空！");
        } catch (Exception e) {
            throw new BusinessParamCheckingException(CAR_MODEL_PARAM_ERROR, e.getMessage());
        }
    }

    
}
