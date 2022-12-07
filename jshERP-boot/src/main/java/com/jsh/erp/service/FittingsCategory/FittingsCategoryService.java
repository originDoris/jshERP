package com.jsh.erp.service.FittingsCategory;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.datasource.entities.FittingsCategory;
import com.jsh.erp.datasource.entities.FittingsCategory;
import com.jsh.erp.datasource.mappers.CarModelMapper;
import com.jsh.erp.datasource.mappers.FittingsCategoryMapper;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.datasource.query.FittingsCategoryQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.FittingsCategory.excel.CategoryExcel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: FittingsCategory
 * @Description:
 * @date: 2022/12/7 15:43
 */
@Service
@Slf4j
public class FittingsCategoryService {


    @Resource
    private FittingsCategoryMapper fittingsCategoryMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletResponse response;

    @Resource
    private SequenceService sequenceService;

    public static final String CODE_PREFIX = "PJLB";


    public IPage<FittingsCategory> queryList(FittingsCategoryQuery fittingsCategoryQuery) {
        Page<FittingsCategory> page = new Page<>(fittingsCategoryQuery.getPageNum(), fittingsCategoryQuery.getPageSize());
        return fittingsCategoryMapper.queryList(page, fittingsCategoryQuery);
    }

    public FittingsCategory detail(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CATEGORY_ID_IS_NULL_CODE, CATEGORY_ID_IS_NULL_MSG);
        }
        return fittingsCategoryMapper.detail(id);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean save(FittingsCategory fittingsCategory) throws Exception {
        verifyParam(fittingsCategory);
        Long userId = userService.getUserId(request);
        fittingsCategory.setCreator(userId);
        fittingsCategory.setCreateTime(new Date());
        if (StringUtils.isBlank(fittingsCategory.getCode())) {
            String code = sequenceService.buildOnlyNumber(CODE_PREFIX);
            fittingsCategory.setCode(code);
        }
        return fittingsCategoryMapper.save(fittingsCategory);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean batchSave(List<FittingsCategory> carModels) throws Exception {
        if (carModels == null || carModels.isEmpty()) {
            throw new BusinessParamCheckingException(CATEGORY_LIST_IS_NULL_CODE, CATEGORY_LIST_IS_NULL_MSG);
        }
        for (FittingsCategory fittingsCategory : carModels) {
            verifyParam(fittingsCategory);
            Long userId = userService.getUserId(request);
            fittingsCategory.setCreator(userId);
            fittingsCategory.setCreateTime(new Date());
            if (StringUtils.isBlank(fittingsCategory.getCode())) {
                String code = sequenceService.buildOnlyNumber(CODE_PREFIX);
                fittingsCategory.setCode(code);
            }
        }
        return fittingsCategoryMapper.batchSave(carModels);
    }

    public boolean remove(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CATEGORY_ID_IS_NULL_CODE, CATEGORY_ID_IS_NULL_MSG);
        }
        return fittingsCategoryMapper.delete(id);
    }


    public boolean batchRemove(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(CATEGORY_ID_LIST_IS_NULL_CODE, CATEGORY_ID_LIST_IS_NULL_MSG);
        }
        return fittingsCategoryMapper.batchDelete(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean modify(FittingsCategory fittingsCategory) throws Exception {
        verifyParam(fittingsCategory);
        if (fittingsCategory.getId() == null) {
            throw new BusinessParamCheckingException(CATEGORY_ID_IS_NULL_CODE, CATEGORY_ID_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        fittingsCategory.setModifier(userId);
        fittingsCategory.setUpdateTime(new Date());
        return fittingsCategoryMapper.modify(fittingsCategory);
    }

    private void verifyParam(FittingsCategory fittingsCategory) throws BusinessParamCheckingException {
        try {
            Assert.notNull(fittingsCategory, "配件类别参数不能为空！");
            Assert.notNull(fittingsCategory.getCategoryName(), "类别名称不能为空！");
        } catch (Exception e) {
            throw new BusinessParamCheckingException(CATEGORY_PARAM_ERROR, e.getMessage());
        }
    }


    /**
     * 导入数据
     * @param multipartFile excel文件
     * @return
     * @throws IOException
     */
    public boolean importData(MultipartFile multipartFile) throws IOException {
        EasyExcelUtil.read(multipartFile.getInputStream(), CategoryExcel.class, categoryExcels -> {
            if (categoryExcels.isEmpty()) {
                return;
            }
            ArrayList<FittingsCategory> fittingsCategories = new ArrayList<>();
            for (CategoryExcel categoryExcel : categoryExcels) {
                FittingsCategory fittingsCategory = new FittingsCategory();
                BeanUtils.copyProperties(categoryExcel, fittingsCategory);
                fittingsCategories.add(fittingsCategory);
            }
            try {
                batchSave(fittingsCategories);
            } catch (Exception e) {
                log.info("批量保存数据出现错误，", e);
                throw new RuntimeException(e);
            }
        });
        return true;
    }

    /**
     * 导出数据
     * @param ids 数据列表
     * @throws BusinessParamCheckingException
     */
    public void downloadData(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(CATEGORY_ID_LIST_IS_NULL_CODE, CATEGORY_ID_LIST_IS_NULL_MSG);
        }
        List<FittingsCategory> fittingsCategories = fittingsCategoryMapper.queryByIds(ids);
        if (fittingsCategories == null || fittingsCategories.isEmpty()) {
            throw new BusinessParamCheckingException(DOWNLOAD_CATEGORY_IS_NULL_CODE, DOWNLOAD_CATEGORY_IS_NULL_MSG);
        }

        List<CategoryExcel> list = new ArrayList<>();
        for (FittingsCategory fittingsCategory : fittingsCategories) {
            CategoryExcel categoryExcel = new CategoryExcel();
            BeanUtils.copyProperties(fittingsCategory, categoryExcel);
            list.add(categoryExcel);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());

            String fileName = "FittingsCategory"+date + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            EasyExcel.write(response.getOutputStream(), CategoryExcel.class).sheet("模板").doWrite(list);
        } catch (Exception e) {
            log.error("车型信息下载失败", e);
            throw new BusinessParamCheckingException(DOWNLOAD_CATEGORY_FAIL_CODE, DOWNLOAD_CATEGORY_FAIL_MSG);
        }

    }
}
