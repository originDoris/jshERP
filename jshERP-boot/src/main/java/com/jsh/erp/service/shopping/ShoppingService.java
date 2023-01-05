package com.jsh.erp.service.shopping;

import com.alibaba.fastjson.JSON;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.entities.shopping.*;
import com.jsh.erp.datasource.mappers.MaterialCategoryMapper;
import com.jsh.erp.datasource.mappers.MaterialMapperEx;
import com.jsh.erp.datasource.query.CartQuery;
import com.jsh.erp.datasource.query.ShoppingQuery;
import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import com.jsh.erp.exception.BusinessParamCheckingException;
import com.jsh.erp.service.carModel.CarModelService;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.product.ProductService;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jsh.erp.constants.ExceptionConstants.*;

/**
 * @author: origindoris
 * @Title: ShoppingService
 * @Description:
 * @date: 2022/12/15 14:31
 */
@Service
@Slf4j
public class ShoppingService {

    @Resource
    private CarModelService carModelService;

    @Resource
    private MaterialMapperEx materialMapperEx;

    @Resource
    private MaterialCategoryMapper materialCategoryMapper;

    @Resource
    private ProductService productService;

    @Resource
    private CartService cartService;

    @Resource
    private UserService userService;

    @Resource
    private RedisService redisService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private DepotHeadService depotHeadService;

    @Resource
    private DepotItemService depotItemService;

    @Resource
    private SequenceService sequenceService;

    @Resource
    private AddressService addressService;

    @Resource
    private HistoryService historyService;

    public static final String HEAD_CODE = "XSDD";


    public Long getCount(Boolean statusFlag) throws Exception {
        Long userId = userService.getUserId(request);
        return depotHeadService.getCount(statusFlag,userId);
    }


    public OrderDetail queryOrderDetail(Long headId) throws Exception {
        DepotHead depotHead = depotHeadService.getDepotHead(headId);
        List<DepotItemVo4WithInfoEx> infoExList = depotItemService.getDetail(headId);
        List<String> carModeCodes = infoExList.stream().map(DepotItemVo4WithInfoEx::getCarModelCode).distinct().collect(Collectors.toList());
        List<CarModel> carModels = carModelService.queryByCode(carModeCodes);
        Map<String, List<DepotItemVo4WithInfoEx>> materialMap = infoExList.stream().collect(Collectors.groupingBy(DepotItemVo4WithInfoEx::getCarModelCode));
        OrderDetail orderDetail = new OrderDetail();
        BeanUtils.copyProperties(depotHead, orderDetail);
        Address detail = addressService.detail(orderDetail.getAddressId());
        orderDetail.setAddressInfo(detail);
        List<OrderDetailMaterial> materials = new ArrayList<>();
        int count = 0;
        for (CarModel carModel : carModels) {
            if (carModel == null) {
                continue;
            }
            List<DepotItemVo4WithInfoEx> infoExes = materialMap.get(carModel.getCode());
            if (infoExes == null || infoExes.isEmpty()) {
                continue;
            }
            OrderDetailMaterial orderDetailMaterial = new OrderDetailMaterial();
            BeanUtils.copyProperties(carModel, orderDetailMaterial);

            List<OrderMaterial> materialList = new ArrayList<>();
            for (DepotItemVo4WithInfoEx infoEx : infoExes) {
                OrderMaterial orderMaterial = new OrderMaterial();
                BeanUtils.copyProperties(infoEx, orderMaterial);
                count = count + orderMaterial.getBasicNumber().intValue();
                materialList.add(orderMaterial);
            }
            orderDetailMaterial.setMaterials(materialList);

            materials.add(orderDetailMaterial);
        }
        if ("0".equals(orderDetail.getStatus()) || "1".equals(orderDetail.getStatus())) {
            orderDetail.setOrderStatus("0");
            orderDetail.setOrderStatusName("未发货");
        }else{
            orderDetail.setOrderStatus("1");
            orderDetail.setOrderStatusName("已发货");
        }
        orderDetail.setOrderDetail(materials);
        orderDetail.setCount(count);
        return orderDetail;
    }


    public List<OrderInfo> queryOrderList(Boolean statusFlag) throws Exception {
        Long userId = userService.getUserId(request);
        List<DepotHeadVo4List> list = depotHeadService.queryOrderList(statusFlag,userId);
        Map<Long, DepotHeadVo4List> categoryMap = list.stream().collect(Collectors.toMap(DepotHeadVo4List::getId, v -> v, (k1, k2) -> k1));
        Set<Long> ids = categoryMap.keySet();
        List<DepotItemVo4WithInfoEx> infoExList = depotItemService.getDetailByIds(new ArrayList<>(ids));
        List<String> carModeCodes = infoExList.stream().map(DepotItemVo4WithInfoEx::getCarModelCode).distinct().collect(Collectors.toList());
        List<CarModel> carModels = carModelService.queryByCode(carModeCodes);
        Map<String, CarModel> carModelMap = carModels.stream().collect(Collectors.toMap(CarModel::getCode, v -> v, (k1, k2) -> k1));

        Map<Long, List<DepotItemVo4WithInfoEx>> materialMap = infoExList.stream().collect(Collectors.groupingBy(DepotItemVo4WithInfoEx::getHeaderId));


        List<Long> addressIds = list.stream().map(DepotHead::getAddressId).collect(Collectors.toList());

        List<Address> addresses = addressService.queryByIds(addressIds);
        Map<Long, Address> addressMap = addresses.stream().collect(Collectors.toMap(Address::getId, v -> v, (k1, k2) -> k1));

        List<OrderInfo> result = new ArrayList<>();
        int count = 0;
        for (DepotHeadVo4List depotHeadVo4List : list) {
            OrderInfo orderInfo = new OrderInfo();
            List<DepotItemVo4WithInfoEx> depotItemVo4WithInfoExes = materialMap.get(depotHeadVo4List.getId());
            if (depotItemVo4WithInfoExes == null || depotItemVo4WithInfoExes.isEmpty()) {
                continue;
            }
            BeanUtils.copyProperties(depotHeadVo4List, orderInfo);

            List<OrderMaterial> orderMaterials = new ArrayList<>();
            for (DepotItemVo4WithInfoEx depotItemVo4WithInfoEx : depotItemVo4WithInfoExes) {
                OrderMaterial orderMaterial = new OrderMaterial();
                BeanUtils.copyProperties(depotItemVo4WithInfoEx, orderMaterial);
                orderMaterials.add(orderMaterial);
                count = count + orderMaterial.getBasicNumber().intValue();
                CarModel carModel = carModelMap.get(depotItemVo4WithInfoEx.getCarModelCode());
                if (carModel == null) {
                    continue;
                }
                orderMaterial.setCarModel(carModel);
            }
            orderInfo.setOrderMaterials(orderMaterials);
            if ("0".equals(orderInfo.getStatus()) || "1".equals(orderInfo.getStatus())) {
                orderInfo.setOrderStatus("0");
                orderInfo.setOrderStatusName("未发货");
            }else{
                orderInfo.setOrderStatus("1");
                orderInfo.setOrderStatusName("已发货");
            }
            result.add(orderInfo);
            orderInfo.setCount(count);
            Address address = addressMap.get(orderInfo.getAddressId());
            orderInfo.setAddressInfo(address);
        }
        return result;
    }






    public SalesOrderInfo getSalesOrder(SalesOrderParam order) throws Exception {
        List<Cart> cartParams = order.getCarts();
        List<Long> ids = cartParams.stream().map(Cart::getCartId).collect(Collectors.toList());
        List<Cart> carts = cartService.queryByIds(ids);
        CartTotal cartTotal = buildCartDto(carts);
        SalesOrderInfo salesOrderInfo = new SalesOrderInfo();
        salesOrderInfo.setCarts(cartTotal.getCartDTOS());
        salesOrderInfo.setTime(new Date());
        String headCode = sequenceService.buildOnlyNumber(HEAD_CODE);
        salesOrderInfo.setCode(headCode);
        salesOrderInfo.setTotalPrice(cartTotal.getTotalPrice());
        salesOrderInfo.setTotalCount(cartTotal.getTotalCount().intValue());
        return salesOrderInfo;
    }






    public boolean generateSalesOrder(SalesOrderParam order) throws Exception {
        List<Cart> carts = order.getCarts();
        List<Long> cartIds = carts.stream().map(Cart::getCartId).collect(Collectors.toList());
        carts = cartService.queryByIds(cartIds);
        Map<Long, Cart> categoryMap = carts.stream().collect(Collectors.toMap(Cart::getMaterialId, v -> v, (k1, k2) -> k1));
        Set<Long> materialIds = categoryMap.keySet();
        List<MaterialVo4Unit> materialVo4Units = materialMapperEx.selectByConditionMaterial(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, new ArrayList<>(materialIds));
        if (materialVo4Units == null || materialVo4Units.isEmpty()) {
            log.info("商品信息不存在！");
            return false;
        }

        List<DepotItem> depotItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (MaterialVo4Unit materialVo4Unit : materialVo4Units) {
            Long id = materialVo4Unit.getId();
            Cart cart = categoryMap.get(id);
            if (cart == null) {
                continue;
            }
            Long count = cart.getCount();
            BigDecimal wholesaleDecimal = materialVo4Unit.getWholesaleDecimal();
            BigDecimal total = new BigDecimal(count);
            BigDecimal multiply = total.multiply(wholesaleDecimal);
            totalPrice = totalPrice.add(multiply);
            DepotItem depotItem = new DepotItem();
            depotItem.setBarCode(materialVo4Unit.getmBarCode());
            depotItem.setUnit(materialVo4Unit.getUnit());
            depotItem.setOperNumber(total);
            depotItem.setUnitPrice(wholesaleDecimal);
            depotItem.setAllPrice(multiply);
            depotItem.setTaxLastMoney(multiply);

            depotItems.add(depotItem);
        }

        // 生成head
        DepotHead depotHead = generateHead(order, totalPrice);
        depotHeadService.addDepotHeadAndDetail(JSON.toJSONString(depotHead), JSON.toJSONString(depotItems), request);

        cartService.batchRemove(cartIds);
        return true;
    }


    private DepotHead generateHead(SalesOrderParam order, BigDecimal totalPrice) throws Exception {
        String headCode = order.getCode();
        if (StringUtils.isBlank(headCode)) {
            headCode = sequenceService.buildOnlyNumber(HEAD_CODE);
        }
        DepotHead depotHead = new DepotHead();
        depotHead.setDefaultNumber(headCode);
        depotHead.setNumber(headCode);
        depotHead.setStatus("0");
        depotHead.setTotalPrice(totalPrice);
        depotHead.setDiscountLastMoney(totalPrice);
        Object userId = redisService.getObjectFromSessionByKey(request, "userId");
        if (userId == null) {
            throw new BusinessParamCheckingException(NOT_LOGIN_CODE, NOT_LOGIN_MSG);
        }
        depotHead.setOrganId(Long.parseLong(userId.toString()));
        depotHead.setType("其它");
        depotHead.setSubType("销售订单");
        depotHead.setOperTime(new Date());
        depotHead.setAddressId(order.getAddressId());
        depotHead.setDiscount(BigDecimal.ZERO);
        depotHead.setDiscountMoney(BigDecimal.ZERO);
        depotHead.setChangeAmount(BigDecimal.ZERO);
        return depotHead;
    }


    public CarModelCategory queryCommodityList(ShoppingQuery shoppingQuery) throws Exception {
        Long userId = userService.getUserId(request);
        CarModelCategory result = new CarModelCategory();
        CarModel carModel = carModelService.detailCode(shoppingQuery.getCarModelCode());
        if (carModel == null) {
            throw new BusinessParamCheckingException(CAR_MODEL_IS_NULL_CODE, CAR_MODEL_IS_NULL_MSG);
        }
        BeanUtils.copyProperties(carModel, result);
        result.setCategories(new ArrayList<>());
        List<MaterialVo4Unit> materialVo4Units = materialMapperEx.selectByConditionMaterial(shoppingQuery.getName(), null, null, null, null, null, null, null, null, null, null, null, null, null, shoppingQuery.getCarModelCode(), null);
        if (materialVo4Units == null || materialVo4Units.isEmpty()) {
            return result;
        }
        List<Long> categoryIds = materialVo4Units.stream().map(Material::getCategoryId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return result;
        }
        List<MaterialCategory> categories = materialCategoryMapper.queryByIds(categoryIds);
        if (categories == null || categories.isEmpty()) {
            return result;
        }
        ArrayList<ShoppingCategory> shoppingCategories = new ArrayList<>();


        Map<Long, MaterialCategory> categoryMap = categories.stream().collect(Collectors.toMap(MaterialCategory::getId, v -> v, (k1, k2) -> k1));
        Map<Long, List<MaterialVo4Unit>> materialMap = materialVo4Units.stream().collect(Collectors.groupingBy(MaterialVo4Unit::getCategoryId));

        for (Map.Entry<Long, List<MaterialVo4Unit>> entry : materialMap.entrySet()) {
            Long key = entry.getKey();
            MaterialCategory materialCategory = categoryMap.get(key);
            if (materialCategory == null) {
                continue;
            }
            List<MaterialVo4Unit> value = entry.getValue();
            ShoppingCategory shoppingCategory = new ShoppingCategory();
            BeanUtils.copyProperties(materialCategory, shoppingCategory);
            shoppingCategory.setMaterials(value);
            shoppingCategories.add(shoppingCategory);
        }
        result.setCategories(shoppingCategories);
        historyService.save(shoppingQuery.getCarModelCode(), userId);
        return result;
    }


    /**
     * 出库
     * @param code 商品代码
     * @return
     * @throws Exception
     */
    public boolean stockOut(String code) throws Exception {
        Product detail = productService.detail(code);
        if (detail == null) {
            throw new BusinessParamCheckingException(PRODUCT_NON_EXIST_CODE, PRODUCT_NON_EXIST_MSG);
        }
        String status = detail.getStatus();
        if (!"2".equals(status)) {
            throw new BusinessParamCheckingException(PRODUCT_STATUS_INCORRECT_CODE, PRODUCT_STATUS_INCORRECT_MSG);
        }
        detail.setStatus("3");
        return productService.modify(detail);
    }


    public Product productDetail(String code) throws BusinessParamCheckingException {
        return productService.detail(code);
    }

    public boolean warehousing(Product product) throws Exception {
        return productService.save(product);
    }

    public CarModel detailByVin(String vin) throws BusinessParamCheckingException {
        return carModelService.detailVin(vin);
    }


    /**
     * 加购物车
     * @param cart
     * @return
     * @throws Exception
     */
    public boolean addCart(Cart cart) throws Exception {
        Long materialId = cart.getMaterialId();
        Long userId = userService.getUserId(request);
        Cart detail = cartService.getByMaterialId(materialId, userId);
        if (detail == null) {
            return cartService.save(cart);
        }
        detail.setCount(detail.getCount() + cart.getCount());
        return cartService.modify(detail);
    }

    /**
     * 设置购物车商品数量
     * @param cart
     * @return
     * @throws Exception
     */
    public boolean setCount(Cart cart) throws Exception {
        Long materialId = cart.getMaterialId();
        Long userId = userService.getUserId(request);
        Cart detail = cartService.getByMaterialId(materialId, userId);
        if (detail == null) {
            return cartService.save(cart);
        }
        detail.setCount(cart.getCount());
        return cartService.modify(detail);
    }

    public boolean removeCart(Long id) throws BusinessParamCheckingException {
        return cartService.remove(id);
    }



    public List<CartDTO> queryCart() throws Exception {
        Long userId = userService.getUserId(request);

        CartQuery cartQuery = new CartQuery();
        cartQuery.setOperator(userId);
        List<Cart> carts = cartService.queryList(cartQuery);
        if (carts == null || carts.isEmpty()) {
            return new ArrayList<>();
        }
        return buildCartDto(carts).getCartDTOS();
    }

    public CartTotal buildCartDto(List<Cart> carts) {
        CartTotal cartTotal = new CartTotal();
        List<CartDTO> result = new ArrayList<>();
        BigDecimal totalCount = new BigDecimal(0);
        BigDecimal totalPrice = new BigDecimal(0);
        cartTotal.setCartDTOS(result);

        if (carts == null || carts.isEmpty()) {
            return cartTotal;
        }
        Map<Long, Cart> categoryMap = carts.stream().collect(Collectors.toMap(Cart::getMaterialId, v -> v, (k1, k2) -> k1));
        Set<Long> materialIds = categoryMap.keySet();
        List<MaterialVo4Unit> materialVo4Units = materialMapperEx.selectByConditionMaterial(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, new ArrayList<>(materialIds));
        if (materialVo4Units == null || materialVo4Units.isEmpty()) {
            return cartTotal;
        }
        Map<String, List<MaterialVo4Unit>> materialMap = materialVo4Units.stream().collect(Collectors.groupingBy(MaterialVo4Unit::getCarModelCode));
        Set<String> strings = materialMap.keySet();
        List<CarModel> carModels = carModelService.queryByCode(new ArrayList<>(strings));
        for (CarModel carModel : carModels) {
            CartDTO cartDTO = new CartDTO();
            String carModelCode = carModel.getCode();
            List<MaterialVo4Unit> material = materialMap.get(carModelCode);
            if (material == null) {
                continue;
            }
            BeanUtils.copyProperties(carModel, cartDTO);
            List<CartMaterial> materials = new ArrayList<>();
            for (MaterialVo4Unit materialVo4Unit : material) {
                CartMaterial cartMaterial = new CartMaterial();
                Cart cart = categoryMap.get(materialVo4Unit.getId());
                if (cart == null) {
                    continue;
                }
                BeanUtils.copyProperties(materialVo4Unit, cartMaterial);
                cartMaterial.setCount(cart.getCount());
                BigDecimal count = new BigDecimal(cart.getCount());
                totalCount = totalCount.add(count);
                totalPrice = totalPrice.add(count.multiply(cartMaterial.getWholesaleDecimal()));
                cartMaterial.setCartId(cart.getId());
                materials.add(cartMaterial);
            }
            cartDTO.setMaterials(materials);
            result.add(cartDTO);
        }
        cartTotal.setTotalCount(totalCount);
        cartTotal.setTotalPrice(totalPrice);
        return cartTotal;
    }


    public boolean importAddress(List<WxAddress> wxAddresses) throws Exception {
        if (wxAddresses == null || wxAddresses.isEmpty()) {
            return true;
        }
        Long userId = userService.getUserId(request);
        List<Address> addresses = new ArrayList<>();
        for (WxAddress wxAddress : wxAddresses) {
            if (wxAddress == null) {
                continue;
            }
            Address address = buildAddress(wxAddress, userId);
            addresses.add(address);
        }
        return addressService.batchSave(addresses);
    }

    private Address buildAddress(WxAddress wxAddress,Long userId){
        Address address = new Address();
        address.setCreateTime(new Date());
        address.setDeleteFlag("0");
        address.setDefaultFlag(false);
        address.setName(wxAddress.getUserName());
        address.setPhone(wxAddress.getTelNumber());
        address.setOperator(userId);
        StringBuilder detail = new StringBuilder();
        if (StringUtils.isNotBlank(wxAddress.getProvinceName())) {
            detail.append(wxAddress.getProvinceName()).append(" ");
        }
        if (StringUtils.isNotBlank(wxAddress.getCityName())) {
            detail.append(wxAddress.getCityName()).append(" ");
        }
        if (StringUtils.isNotBlank(wxAddress.getCountyName())) {
            detail.append(wxAddress.getCountyName()).append(" ");
        }
        if (StringUtils.isNotBlank(wxAddress.getDetailInfo())) {
            detail.append("\n").append(wxAddress.getDetailInfo());
        }
        address.setAddress(detail.toString());
        return address;
    }





}
