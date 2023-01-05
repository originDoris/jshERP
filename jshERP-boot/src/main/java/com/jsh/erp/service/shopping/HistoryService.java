package com.jsh.erp.service.shopping;

import com.jsh.erp.datasource.entities.CarModel;
import com.jsh.erp.datasource.entities.shopping.Cart;
import com.jsh.erp.datasource.entities.shopping.History;
import com.jsh.erp.datasource.entities.shopping.HistoryInfo;
import com.jsh.erp.datasource.mappers.CartMapper;
import com.jsh.erp.datasource.mappers.HistoryMapper;
import com.jsh.erp.datasource.query.CartQuery;
import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.carModel.CarModelService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
public class HistoryService {

    @Resource
    private CarModelService carModelService;

    @Resource
    private HistoryMapper historyMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;


    public List<HistoryInfo> queryList() throws Exception {
        Long userId = userService.getUserId(request);
        List<History> histories = historyMapper.queryList(userId);
        if (histories == null || histories.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> codes = histories.stream().map(History::getCarModelCode).distinct().collect(Collectors.toList());
        List<CarModel> carModels = carModelService.queryByCode(codes);
        if (carModels.size() > 10) {
            carModels = carModels.subList(0, 10);
        }
        Map<String, CarModel> categoryMap = carModels.stream().collect(Collectors.toMap(CarModel::getCode, v -> v, (k1, k2) -> k1));

        TreeSet<History> collect = histories.stream().collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(History::getCarModelCode))));
        List<HistoryInfo> historyInfos = new ArrayList<>();
        for (History history : collect) {
            CarModel carModel = categoryMap.get(history.getCarModelCode());
            if (carModel == null) {
                continue;
            }
            HistoryInfo historyInfo = new HistoryInfo();
            BeanUtils.copyProperties(history, historyInfo);
            historyInfo.setCarModel(carModel.getCarModel());
            historyInfos.add(historyInfo);
        }
        return historyInfos;
    }


    public boolean save( String carModelCode, Long userId) {
        History history = new History();
        history.setOperator(userId);
        history.setDeleteFlag("0");
        history.setCarModelCode(carModelCode);
        history.setCreateTime(new Date());
        history.setUpdateTime(new Date());
        return historyMapper.save(history);
    }


    public boolean removeByCodes(List<String> carModelCodes) {
        if (carModelCodes == null || carModelCodes.isEmpty()) {
            return true;
        }
        return historyMapper.removeByCodes(carModelCodes);
    }

    public boolean removeByUserId() throws Exception {
        Long userId = userService.getUserId(request);
        return historyMapper.removeByUserId(userId);
    }
}
