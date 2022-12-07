package com.jsh.erp.service.carModel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.mappers.CarModelMapper;
import com.jsh.erp.datasource.query.CarModelQuery;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.carModel.excel.CarModelReadExcel;
import com.jsh.erp.service.carModel.excel.CarModelWriteExcel;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;
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
 * @Title: CarModelService
 * @Description:
 * @date: 2022/12/6 13:45
 */
@Service
@Slf4j
public class CarModelService {

    @Resource
    private CarModelMapper carModelMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletResponse response;



    public IPage<CarModel> queryList(CarModelQuery carModelQuery) {
        Page<CarModel> carModelPage = new Page<>(carModelQuery.getPageNum(), carModelQuery.getPageSize());
        return carModelMapper.queryList(carModelPage, carModelQuery);
    }

    public CarModel detail(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CAR_MODEL_ID_IS_NULL_CODE, CAR_MODEL_ID_IS_NULL_MSG);
        }
        return carModelMapper.detail(id);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean save(CarModel carModel) throws Exception {
        verifyParam(carModel);
        Long userId = userService.getUserId(request);
        carModel.setCreator(userId);
        carModel.setCreateTime(new Date());
        return carModelMapper.save(carModel);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean batchSave(List<CarModel> carModels) throws Exception {
        if (carModels == null || carModels.isEmpty()) {
            throw new BusinessParamCheckingException(CAR_MODEL_LIST_IS_NULL_CODE, CAR_MODEL_LIST_IS_NULL_MSG);
        }
        for (CarModel carModel : carModels) {
            verifyParam(carModel);
            Long userId = userService.getUserId(request);
            carModel.setCreator(userId);
            carModel.setCreateTime(new Date());
        }
        return carModelMapper.batchSave(carModels);
    }

    public boolean remove(Long id) throws BusinessParamCheckingException {
        if (id == null) {
            throw new BusinessParamCheckingException(CAR_MODEL_ID_IS_NULL_CODE, CAR_MODEL_ID_IS_NULL_MSG);
        }
        return carModelMapper.delete(id);
    }


    public boolean batchRemove(List<Long> ids) throws BusinessParamCheckingException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessParamCheckingException(CAR_MODEL_ID_LIST_IS_NULL_CODE, CAR_MODEL_ID_LIST_IS_NULL_MSG);
        }
        return carModelMapper.batchDelete(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean modify(CarModel carModel) throws Exception {
        verifyParam(carModel);
        if (carModel.getId() == null) {
            throw new BusinessParamCheckingException(CAR_MODEL_ID_IS_NULL_CODE, CAR_MODEL_ID_IS_NULL_MSG);
        }
        Long userId = userService.getUserId(request);
        carModel.setModifier(userId);
        carModel.setUpdateTime(new Date());
        return carModelMapper.modify(carModel);
    }

    private void verifyParam(CarModel carModel) throws BusinessParamCheckingException {
        try {
            Assert.notNull(carModel, "车型参数不能为空！");
            Assert.notNull(carModel.getCarVin(), "车型VIN码不能为空！");
            Assert.notNull(carModel.getCarModel(),"车型不能为空！");
        } catch (Exception e) {
            throw new BusinessParamCheckingException(CAR_MODEL_PARAM_ERROR, e.getMessage());
        }
    }


    /**
     * 导入数据
     * @param multipartFile excel文件
     * @return
     * @throws IOException
     */
    public boolean importData(MultipartFile multipartFile) throws IOException {
        EasyExcelUtil.read(multipartFile.getInputStream(),CarModelReadExcel.class,carModelReadExcels -> {
            if (carModelReadExcels.isEmpty()) {
                return;
            }
            ArrayList<CarModel> carModels = new ArrayList<>();
            for (CarModelReadExcel carModelReadExcel : carModelReadExcels) {
                CarModel carModel = new CarModel();
                BeanUtils.copyProperties(carModelReadExcel, carModel);
                carModels.add(carModel);
            }
            try {
                batchSave(carModels);
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
            throw new BusinessParamCheckingException(CAR_MODEL_ID_LIST_IS_NULL_CODE, CAR_MODEL_ID_LIST_IS_NULL_MSG);
        }
        List<CarModel> carModels = carModelMapper.queryByIds(ids);
        if (carModels == null || carModels.isEmpty()) {
            throw new BusinessParamCheckingException(DOWNLOAD_CAR_MODEL_IS_NULL_CODE, DOWNLOAD_CAR_MODEL_IS_NULL_MSG);
        }

        List<CarModelWriteExcel> list = new ArrayList<>();
        for (CarModel carModel : carModels) {
            CarModelWriteExcel carModelWriteExcel = new CarModelWriteExcel();
            BeanUtils.copyProperties(carModel, carModelWriteExcel);
            list.add(carModelWriteExcel);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String date = sdf.format(new Date());

            String fileName = "CarModel"+date + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            EasyExcel.write(response.getOutputStream(), CarModelWriteExcel.class).sheet("模板").doWrite(list);
        } catch (Exception e) {
            log.error("车型信息下载失败", e);
            throw new BusinessParamCheckingException(DOWNLOAD_CAR_MODEL_FAIL_CODE, DOWNLOAD_CAR_MODEL_FAIL_MSG);
        }

    }
}
