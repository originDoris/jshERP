package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.MaterialInitialStockWithMaterial;
import com.jsh.erp.datasource.entities.MaterialLocationInfo;
import com.jsh.erp.datasource.entities.MaterialVo4Unit;
import com.jsh.erp.datasource.entities.Unit;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.unit.UnitService;
import com.jsh.erp.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsh.erp.constants.ExceptionConstants.SERVICE_SUCCESS_CODE;
import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;

/**
 * @author ji|sheng|hua jshERP
 */
@RestController
@RequestMapping(value = "/material")
@Api(tags = {"商品管理"})
public class MaterialController {
    private Logger logger = LoggerFactory.getLogger(MaterialController.class);

    @Resource
    private MaterialService materialService;

    @Resource
    private DepotItemService depotItemService;

    @Resource
    private UnitService unitService;

    @Resource
    private DepotService depotService;

    @Resource
    private RedisService redisService;

    /**
     * 检查商品是否存在
     * @param id
     * @param name
     * @param model
     * @param color
     * @param standard
     * @param mfrs
     * @param otherField1
     * @param otherField2
     * @param otherField3
     * @param unit
     * @param unitId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkIsExist")
    @ApiOperation(value = "检查商品是否存在")
    public String checkIsExist(@RequestParam("id") Long id, @RequestParam("name") String name,
                               @RequestParam("model") String model, @RequestParam("color") String color,
                               @RequestParam("standard") String standard, @RequestParam("mfrs") String mfrs,
                               @RequestParam("otherField1") String otherField1, @RequestParam("otherField2") String otherField2,
                               @RequestParam("otherField3") String otherField3, @RequestParam("unit") String unit,@RequestParam("unitId") Long unitId,
                               @RequestParam(value = "oem",required = false) String oem,
                               @RequestParam(value = "vin",required = false) String vin,
                               @RequestParam(value = "carModelCode",required = false) String carModelCode,
                               HttpServletRequest request)throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>();
        int exist = materialService.checkIsExist(id, name, StringUtil.toNull(model), StringUtil.toNull(color),
                StringUtil.toNull(standard), StringUtil.toNull(mfrs), StringUtil.toNull(otherField1),
                StringUtil.toNull(otherField2), StringUtil.toNull(otherField3), StringUtil.toNull(unit), unitId, oem, vin, carModelCode);
        if(exist > 0) {
            objectMap.put("status", true);
        } else {
            objectMap.put("status", false);
        }
        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchSetStatus")
    @ApiOperation(value = "批量设置状态-启用或者禁用")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Boolean status = jsonObject.getBoolean("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = materialService.batchSetStatus(status, ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 根据id来查询商品名称
     * @param id
     * @param request
     * @return
     */
    @GetMapping(value = "/findById")
    @ApiOperation(value = "根据id来查询商品名称")
    public BaseResponseInfo findById(@RequestParam("id") Long id, HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MaterialVo4Unit> list = materialService.findById(id);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据meId来查询商品名称
     * @param meId
     * @param request
     * @return
     */
    @GetMapping(value = "/findByIdWithBarCode")
    @ApiOperation(value = "根据meId来查询商品名称")
    public BaseResponseInfo findByIdWithBarCode(@RequestParam("meId") Long meId,
                                                @RequestParam("mpList") String mpList,
                                                HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String[] mpArr = mpList.split(",");
            MaterialVo4Unit mu = new MaterialVo4Unit();
            List<MaterialVo4Unit> list = materialService.findByIdWithBarCode(meId);
            if(list!=null && list.size()>0) {
                mu = list.get(0);
                mu.setMaterialOther(materialService.getMaterialOtherByParam(mpArr, mu));
            }
            res.code = 200;
            res.data = mu;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }


    @GetMapping("/getLocation")
    public BaseResponseInfo getLocation(@RequestParam("barCode") String barCode,
                                        @RequestParam("depotId") Long depotId) {
        List<MaterialLocationInfo> location = materialService.getLocation(depotId, barCode);
        return new BaseResponseInfo(SERVICE_SUCCESS_CODE, location);
    }


    /**
     * 查找商品信息-下拉框
     * @param mpList
     * @param request
     * @return
     */
    @GetMapping(value = "/findBySelect")
    @ApiOperation(value = "查找商品信息")
    public JSONObject findBySelect(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                  @RequestParam(value = "q", required = false) String q,
                                  @RequestParam(value = "mpList",required = false) String mpList,
                                  @RequestParam(value = "depotId", required = false) Long depotId,
                                  @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                                  @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                                  @RequestParam("page") Integer currentPage,
                                  @RequestParam("rows") Integer pageSize,
                                  HttpServletRequest request) throws Exception{
        JSONObject object = new JSONObject();
        try {
            List<MaterialVo4Unit> dataList = materialService.findBySelectWithBarCode(categoryId, q, enableSerialNumber,
                    enableBatchNumber, (currentPage-1)*pageSize, pageSize);
            int total = materialService.findBySelectWithBarCodeCount(categoryId, q, enableSerialNumber,
                    enableBatchNumber);
            object.put("total", total);
            JSONArray dataArray = new JSONArray();
            //存放数据json数组
            if (null != dataList) {
                for (MaterialVo4Unit material : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", material.getMeId()); //商品扩展表的id
                    String ratioStr = ""; //比例
                    Unit unit = new Unit();
                    if (material.getUnitId() == null) {
                        ratioStr = "";
                    } else {
                        unit = unitService.getUnit(material.getUnitId());
                        //拼接副单位的比例
                        String commodityUnit = material.getCommodityUnit();
                        if(commodityUnit.equals(unit.getBasicUnit())) {
                            ratioStr = "[基本]";
                        }
                        if(commodityUnit.equals(unit.getOtherUnit())) {
                            ratioStr = "[" + unit.getRatio() + unit.getBasicUnit() + "]";
                        }
                        if(commodityUnit.equals(unit.getOtherUnitTwo())) {
                            ratioStr = "[" + unit.getRatioTwo() + unit.getBasicUnit() + "]";
                        }
                        if(commodityUnit.equals(unit.getOtherUnitThree())) {
                            ratioStr = "[" + unit.getRatioThree() + unit.getBasicUnit() + "]";
                        }
                    }
                    item.put("mBarCode", material.getmBarCode());
                    item.put("name", material.getName());
                    item.put("categoryName", material.getCategoryName());
                    item.put("standard", material.getStandard());
                    item.put("model", material.getModel());
                    item.put("color", material.getColor());
                    item.put("unit", material.getCommodityUnit() + ratioStr);
                    item.put("sku", material.getSku());
                    item.put("enableSerialNumber", material.getEnableSerialNumber());
                    item.put("enableBatchNumber", material.getEnableBatchNumber());
                    BigDecimal stock;
                    if(StringUtil.isNotEmpty(material.getSku())){
                        stock = depotItemService.getSkuStockByParam(depotId,material.getMeId(),null,null);
                    } else {
                        stock = depotItemService.getStockByParam(depotId,material.getId(),null,null);
                        if (material.getUnitId()!=null){
                            String commodityUnit = material.getCommodityUnit();
                            stock = unitService.parseStockByUnit(stock, unit, commodityUnit);
                        }
                    }
                    item.put("stock", stock);
                    if (mpList != null && !mpList.isEmpty()) {
                        String[] mpArr = mpList.split(",");
                        item.put("expand", materialService.getMaterialOtherByParam(mpArr, material));
                    }
                    item.put("imgName", material.getImgName());
                    dataArray.add(item);
                }
            }
            object.put("rows", dataArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 根据商品id查找商品信息
     * @param meId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialByMeId")
    @ApiOperation(value = "根据商品id查找商品信息")
    public JSONObject getMaterialByMeId(@RequestParam(value = "meId", required = false) Long meId,
                                        @RequestParam("mpList") String mpList,
                                        HttpServletRequest request) throws Exception{
        JSONObject item = new JSONObject();
        try {
            String[] mpArr = mpList.split(",");
            List<MaterialVo4Unit> materialList = materialService.getMaterialByMeId(meId);
            if(materialList!=null && materialList.size()!=1) {
                return item;
            } else if(materialList.size() == 1) {
                MaterialVo4Unit material = materialList.get(0);
                item.put("Id", material.getMeId()); //商品扩展表的id
                String ratio; //比例
                if (material.getUnitId() == null || material.getUnitId().equals("")) {
                    ratio = "";
                } else {
                    ratio = material.getUnitName();
                    ratio = ratio.substring(ratio.indexOf("("));
                }
                //名称/型号/扩展信息/包装
                String MaterialName = "";
                MaterialName = MaterialName + material.getmBarCode() + "_" + material.getName()
                        + ((material.getStandard() == null || material.getStandard().equals("")) ? "" : "(" + material.getStandard() + ")");
                String expand = materialService.getMaterialOtherByParam(mpArr, material); //扩展信息
                MaterialName = MaterialName + expand + ((material.getUnit() == null || material.getUnit().equals("")) ? "" : "(" + material.getUnit() + ")") + ratio;
                item.put("MaterialName", MaterialName);
                item.put("name", material.getName());
                item.put("expand", expand);
                item.put("model", material.getModel());
                item.put("standard", material.getStandard());
                item.put("unit", material.getUnit() + ratio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 生成excel表格
     * @param categoryId
     * @param materialParam
     * @param color
     * @param weight
     * @param expiryNum
     * @param enabled
     * @param enableSerialNumber
     * @param enableBatchNumber
     * @param remark
     * @param mpList
     * @param request
     * @param response
     */
    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "生成excel表格")
    public void exportExcel(@RequestParam(value = "categoryId", required = false) String categoryId,
                            @RequestParam(value = "materialParam", required = false) String materialParam,
                            @RequestParam(value = "color", required = false) String color,
                            @RequestParam(value = "weight", required = false) String weight,
                            @RequestParam(value = "expiryNum", required = false) String expiryNum,
                            @RequestParam(value = "enabled", required = false) String enabled,
                            @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                            @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                            @RequestParam(value = "remark", required = false) String remark,
                            @RequestParam(value = "mpList", required = false) String mpList,
                            @RequestParam(value = "oem",required = false) String oem,
                            @RequestParam(value = "vin",required = false) String vin,
                            @RequestParam(value = "carModelCode",required = false) String carModelCode,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            String[] mpArr = new String[]{};
            if(StringUtil.isNotEmpty(mpList)){
                mpArr= mpList.split(",");
            }
            List<MaterialVo4Unit> dataList = materialService.exportExcel(StringUtil.toNull(materialParam), StringUtil.toNull(color),
                    StringUtil.toNull(weight), StringUtil.toNull(expiryNum), StringUtil.toNull(enabled), StringUtil.toNull(enableSerialNumber),
                    StringUtil.toNull(enableBatchNumber), StringUtil.toNull(remark), StringUtil.toNull(categoryId), oem, vin, carModelCode);
            String[] names = {"条码", "名称", "规格", "型号", "颜色", "类别", "扩展信息", "单位", "基础重量", "保质期", "采购价", "零售价", "销售价", "最低售价", "备注", "状态", "序列号", "批号","OEM","VIN"};
            String title = "商品信息";
            List<String[]> objects = new ArrayList<>();
            if (null != dataList) {
                for (MaterialVo4Unit m : dataList) {
                    String[] objs = new String[100];
                    objs[0] = m.getmBarCode();
                    objs[1] = m.getName();
                    objs[2] = m.getStandard();
                    objs[3] = m.getModel();
                    objs[4] = m.getColor();
                    objs[5] = m.getCategoryName();
                    objs[6] = materialService.getMaterialOtherByParam(mpArr, m);
                    objs[7] = m.getCommodityUnit();
                    objs[8] = m.getWeight() == null? "" : m.getWeight().toString();
                    objs[9] = m.getExpiryNum() == null? "" : m.getExpiryNum().toString();
                    objs[10] = m.getPurchaseDecimal() == null? "" : m.getPurchaseDecimal().toString();
                    objs[11] = m.getCommodityDecimal() == null? "" : m.getCommodityDecimal().toString();
                    objs[12] = m.getWholesaleDecimal() == null? "" : m.getWholesaleDecimal().toString();
                    objs[13] = m.getLowDecimal() == null? "" : m.getLowDecimal().toString();
                    objs[14] = m.getRemark();
                    objs[15] = m.getEnabled() ? "启用" : "禁用";
                    objs[16] = "1".equals(m.getEnableSerialNumber()) ? "有" : "无";
                    objs[17] = "1".equals(m.getEnableBatchNumber()) ? "有" : "无";
                    objects.add(objs);
                }
            }
            File file = ExcelUtils.exportObjectsWithoutTitle(title, names, title, objects);
            ExportExecUtil.showExec(file, file.getName(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * excel表格导入产品（含初始库存）
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importExcel")
    @ApiOperation(value = "excel表格导入产品")
    public BaseResponseInfo importExcel(MultipartFile file,
                            HttpServletRequest request, HttpServletResponse response) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            res = materialService.importExcel(file, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public BigDecimal parseBigDecimalEx(String str)throws Exception{
        if(!StringUtil.isEmpty(str)) {
            return  new BigDecimal(str);
        } else {
            return null;
        }
    }

    /**
     * 获取商品序列号
     * @param q
     * @param currentPage
     * @param pageSize
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialEnableSerialNumberList")
    @ApiOperation(value = "获取商品序列号")
    public JSONObject getMaterialEnableSerialNumberList(
                                @RequestParam(value = "q", required = false) String q,
                                @RequestParam("page") Integer currentPage,
                                @RequestParam("rows") Integer pageSize,
                                HttpServletRequest request,
                                HttpServletResponse response)throws Exception {
        JSONObject object= new JSONObject();
        try {
            List<MaterialVo4Unit> list = materialService.getMaterialEnableSerialNumberList(q, (currentPage-1)*pageSize, pageSize);
            Long count = materialService.getMaterialEnableSerialNumberCount(q);
            object.put("rows", list);
            object.put("total", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 获取最大条码
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaxBarCode")
    @ApiOperation(value = "获取最大条码")
    public BaseResponseInfo getMaxBarCode() throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        String barCode = materialService.getMaxBarCode();
        map.put("barCode", barCode);
        res.code = 200;
        res.data = map;
        return res;
    }

    /**
     * 商品名称模糊匹配
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialNameList")
    @ApiOperation(value = "商品名称模糊匹配")
    public JSONArray getMaterialNameList() throws Exception {
        JSONArray arr = new JSONArray();
        try {
            List<String> list = materialService.getMaterialNameList();
            for (String s : list) {
                JSONObject item = new JSONObject();
                item.put("value", s);
                item.put("text", s);
                arr.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 根据条码查询商品信息
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialByBarCode")
    @ApiOperation(value = "根据条码查询商品信息")
    public BaseResponseInfo getMaterialByBarCode(@RequestParam("barCode") String barCode,
                                          @RequestParam(value = "depotId", required = false) Long depotId,
                                          @RequestParam("mpList") String mpList,
                                          @RequestParam(required = false, value = "prefixNo") String prefixNo,
                                          HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String[] mpArr = mpList.split(",");
            List<MaterialVo4Unit> list = materialService.getMaterialByBarCode(barCode);
            if(list!=null && list.size()>0) {
                for(MaterialVo4Unit mvo: list) {
                    mvo.setMaterialOther(materialService.getMaterialOtherByParam(mpArr, mvo));
                    if ("LSCK".equals(prefixNo) || "LSTH".equals(prefixNo)) {
                        //零售价
                        mvo.setBillPrice(mvo.getCommodityDecimal());
                    } else if ("CGDD".equals(prefixNo) || "CGRK".equals(prefixNo) || "CGTH".equals(prefixNo)
                            || "QTRK".equals(prefixNo) || "DBCK".equals(prefixNo) || "ZZD".equals(prefixNo) || "CXD".equals(prefixNo)
                            || "PDLR".equals(prefixNo) || "PDFP".equals(prefixNo)) {
                        //采购价
                        mvo.setBillPrice(mvo.getPurchaseDecimal());
                    } else if ("XSDD".equals(prefixNo) || "XSCK".equals(prefixNo) || "XSTH".equals(prefixNo) || "QTCK".equals(prefixNo)) {
                        //销售价
                        mvo.setBillPrice(mvo.getWholesaleDecimal());
                    }
                    //仓库id
                    if (depotId == null) {
                        JSONArray depotArr = depotService.findDepotByCurrentUser();
                        for (Object obj : depotArr) {
                            JSONObject depotObj = JSONObject.parseObject(obj.toString());
                            if (depotObj.get("isDefault") != null) {
                                Boolean isDefault = depotObj.getBoolean("isDefault");
                                if (isDefault) {
                                    Long id = depotObj.getLong("id");
                                    if (!"CGDD".equals(prefixNo) && !"XSDD".equals(prefixNo)) {
                                        //除订单之外的单据才有仓库
                                        mvo.setDepotId(id);
                                    }
                                    getStockByMaterialInfo(mvo);
                                }
                            }
                        }
                    } else {
                        mvo.setDepotId(depotId);
                        getStockByMaterialInfo(mvo);
                    }
                }
            }
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据商品信息获取库存，进行赋值
     * @param mvo
     * @throws Exception
     */
    private void getStockByMaterialInfo(MaterialVo4Unit mvo) throws Exception {
        BigDecimal stock;
        if (StringUtil.isNotEmpty(mvo.getSku())) {
            stock = depotItemService.getSkuStockByParam(mvo.getDepotId(), mvo.getMeId(), null, null);
        } else {
            stock = depotItemService.getStockByParam(mvo.getDepotId(), mvo.getId(), null, null);
            if (mvo.getUnitId() != null) {
                Unit unit = unitService.getUnit(mvo.getUnitId());
                String commodityUnit = mvo.getCommodityUnit();
                stock = unitService.parseStockByUnit(stock, unit, commodityUnit);
            }
        }
        mvo.setStock(stock);
    }

    /**
     * 商品库存查询
     * @param currentPage
     * @param pageSize
     * @param depotIds
     * @param categoryId
     * @param materialParam
     * @param mpList
     * @param column
     * @param order
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getListWithStock")
    @ApiOperation(value = "商品库存查询")
    public BaseResponseInfo getListWithStock(@RequestParam("currentPage") Integer currentPage,
                                             @RequestParam("pageSize") Integer pageSize,
                                             @RequestParam(value = "depotIds", required = false) String depotIds,
                                             @RequestParam(value = "categoryId", required = false) Long categoryId,
                                             @RequestParam("materialParam") String materialParam,
                                             @RequestParam("zeroStock") Integer zeroStock,
                                             @RequestParam("mpList") String mpList,
                                             @RequestParam("column") String column,
                                             @RequestParam("order") String order,
                                             @RequestParam(value = "oem",required = false) String oem,
                                             @RequestParam(value = "vin",required = false) String vin,
                                             @RequestParam(value = "carModelCode",required = false) String carModelCode,
                                             HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
            List<Long> idList = new ArrayList<>();
            List<Long> depotList = new ArrayList<>();
            if(categoryId != null){
                idList = materialService.getListByParentId(categoryId);
            }
            if(StringUtil.isNotEmpty(depotIds)) {
                depotList = StringUtil.strToLongList(depotIds);
            } else {
                //未选择仓库时默认为当前用户有权限的仓库
                JSONArray depotArr = depotService.findDepotByCurrentUser();
                for(Object obj: depotArr) {
                    JSONObject object = JSONObject.parseObject(obj.toString());
                    depotList.add(object.getLong("id"));
                }
            }
            List<MaterialVo4Unit> dataList = materialService.getListWithStock(depotList, idList, StringUtil.toNull(materialParam), zeroStock,
                    StringUtil.safeSqlParse(column), StringUtil.safeSqlParse(order), (currentPage - 1) * pageSize, pageSize, oem, vin, carModelCode);
            int total = materialService.getListWithStockCount(depotList, idList, StringUtil.toNull(materialParam), zeroStock, oem, vin, carModelCode);
            MaterialVo4Unit materialVo4Unit= materialService.getTotalStockAndPrice(depotList, idList, StringUtil.toNull(materialParam));
            map.put("total", total);
            map.put("currentStock", materialVo4Unit.getCurrentStock());
            map.put("currentStockPrice", materialVo4Unit.getCurrentStockPrice());
            map.put("rows", dataList);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 批量设置商品当前的实时库存（按每个仓库）
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchSetMaterialCurrentStock")
    @ApiOperation(value = "批量设置商品当前的实时库存（按每个仓库）")
    public String batchSetMaterialCurrentStock(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = materialService.batchSetMaterialCurrentStock(ids);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 批量更新商品信息
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchUpdate")
    @ApiOperation(value = "批量更新商品信息")
    public String batchUpdate(@RequestBody JSONObject jsonObject,
                              HttpServletRequest request)throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int res = materialService.batchUpdate(jsonObject);
        if(res > 0) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
