package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.shopping.History;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: origindoris
 * @Title: CarModelMapper
 * @Description:
 * @date: 2022/12/6 10:16
 */
public interface HistoryMapper {
    List<History> queryList(@Param("userId") Long userId);

    boolean save(History history);

    boolean removeByCodes(@Param("carModelCodes") List<String> removeByCodes);

    boolean removeByUserId(@Param("userId") Long userId);

}
