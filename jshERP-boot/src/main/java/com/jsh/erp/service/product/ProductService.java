package com.jsh.erp.service.product;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.datasource.entities.Product;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.ProductMapper;
import com.jsh.erp.datasource.mappers.StockCheckMapper;
import com.jsh.erp.datasource.query.ProductQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.QRUtil;
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
import java.math.BigDecimal;
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
public class ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletResponse response;

    @Resource
    private SequenceService sequenceService;


    public static final String CODE_PREFIX = "SP";



    public IPage<Product> queryList(ProductQuery productQuery) {
        Page<Product> stockCheckPage = new Page<>(productQuery.getPageNum(), productQuery.getPageSize());
        return productMapper.queryList(stockCheckPage, productQuery);
    }

    public Product detail(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(PRODUCT_ID_IS_NULL_CODE, PRODUCT_ID_IS_NULL_MSG);
        }
        return productMapper.detail(id);
    }

    public Product detail(String code) throws BusinessParamCheckingException {
        if (code == null) {
            throw new BusinessParamCheckingException(PRODUCT_CODE_IS_NULL_CODE, PRODUCT_CODE_IS_NULL_MSG);
        }
        return productMapper.detailByCode(code);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean save(Product product) throws Exception {
        verifyParam(product);
        Long userId = userService.getUserId(request);
        User user = userService.getUser(userId);
        product.setTenantId(user.getTenantId());
        product.setOperator(userId);
        product.setCreateTime(new Date());
        product.setDeleteFlag("0");
        if (StringUtils.isBlank(product.getCode())) {
            String code = sequenceService.buildOnlyNumber(CODE_PREFIX);
            product.setCode(code);
        }
        return productMapper.save(product);
    }
    


    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean batchSave(List<Product> carModels) throws Exception {
        if (carModels == null || carModels.isEmpty()) {
            throw new BusinessParamCheckingException(PRODUCT_ID_LIST_IS_NULL_CODE, PRODUCT_ID_LIST_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        User user = userService.getUser(userId);
        for (Product product : carModels) {
            verifyParam(product);
            product.setTenantId(user.getTenantId());
            product.setOperator(userId);
            product.setDeleteFlag("0");
            product.setCreateTime(new Date());
            if (StringUtils.isBlank(product.getCode())) {
                String code = sequenceService.buildOnlyNumber(CODE_PREFIX);
                product.setCode(code);
            }
        }
        return productMapper.batchSave(carModels);
    }

    public boolean remove(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(PRODUCT_ID_IS_NULL_CODE, PRODUCT_ID_IS_NULL_MSG);
        }
        return productMapper.delete(id);
    }


    public boolean batchRemove(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(PRODUCT_ID_LIST_IS_NULL_CODE, PRODUCT_ID_LIST_IS_NULL_MSG);
        }
        return productMapper.batchDelete(ids);
    }

    public boolean batchRemoveByCode(List<String> codes) throws BusinessParamCheckingException {
        if (codes == null || codes.isEmpty()) {
            return false;
        }
        return productMapper.batchDeleteByCode(codes);
    }


    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean modify(Product product) throws Exception {
        verifyParam(product);
        if (product.getId() == null) {
            throw new BusinessParamCheckingException(PRODUCT_ID_IS_NULL_CODE, PRODUCT_ID_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        User user = userService.getUser(userId);
        product.setTenantId(user.getTenantId());
        product.setOperator(userId);
        product.setUpdateTime(new Date());
        return productMapper.modify(product);
    }


    public boolean modifyHeadCode(List<String> codes, String headCode,String status) {
        if (codes == null || codes.isEmpty()) {
            return false;
        }
        if ("2".equals(status)) {
            return productMapper.modifyInHeadCode(codes, headCode);
        }else{
            return productMapper.modifyOutHeadCode(codes, headCode);
        }

    }

    private void verifyParam(Product product) throws BusinessParamCheckingException {
        try {
            Assert.notNull(product, "商品参数不能为空！");
            Assert.notNull(product.getProductName(), "商品名称不能为空！");
        } catch (Exception e) {
            throw new BusinessParamCheckingException(PRODUCT_PARAM_ERROR, e.getMessage());
        }
    }


    public List<Product> queryByStatus(ProductQuery productQuery){
        return productMapper.queryByStatus(productQuery);
    }

    /**
     * 生成二维码
     * @param code 单据信息
     * @throws Exception
     */
    public void generateQR(String code) throws Exception {
        Product detail = detail(code);
        if (detail == null) {
            throw new BusinessParamCheckingException(PRODUCT_IS_NULL_CODE, PRODUCT_IS_NULL_MSG);
        }
        detail.setQrFlag(true);
        modify(detail);
        BufferedImage image = QRUtil.generateQrCode(code);
        // 禁止图像缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        try(ServletOutputStream sos = response.getOutputStream()){
            ImageIO.write(image, "jpeg", sos);
        } catch (IOException e) {
            log.info("返回二维码失败！", e);
            throw new BusinessParamCheckingException(PRODUCT_QR_FAIL_CODE, e.getMessage());
        }
    }

    
}
