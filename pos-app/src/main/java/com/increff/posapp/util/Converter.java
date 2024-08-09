package com.increff.posapp.util;

import com.increff.posapp.model.*;
import com.increff.posapp.pojo.*;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.InventoryService;
import org.apache.log4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Converter {
	private Converter(){}
	private static final Logger logger = Logger.getLogger(Converter.class);
	public static BrandData convertToBrandData(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrand(p.getBrand());
		d.setCategory(p.getCategory());
		d.setId(p.getId());
		return d;
	}

	public static List<BrandData> convertToBrandDataList(List<BrandPojo> pojos) {
		List<BrandData> dataList = new ArrayList<>();
		for(BrandPojo p: pojos){
			BrandData data = new BrandData();
			data.setId(p.getId());
			data.setBrand(p.getBrand());
			data.setCategory(p.getCategory());
			dataList.add(data);
		}
		return dataList;
	}

	public static BrandPojo convertToBrandPojo(BrandForm f) {
		BrandPojo p = new BrandPojo();
		p.setBrand(f.getBrand());
		p.setCategory(f.getCategory());
		return p;
	}
	
	public static ProductData convertToProductData(ProductPojo productPojo, BrandPojo brandPojo) {
		ProductData productData = new ProductData();
		productData.setId(productPojo.getId());
		productData.setBarcode(productPojo.getBarcode());
		productData.setBrand(brandPojo.getBrand());
		productData.setCategory(brandPojo.getCategory());
		productData.setBrandCategory(productPojo.getBrandCategory());
		productData.setName(productPojo.getName());
		productData.setMrp(productPojo.getMrp());
		return productData;
	}

	public static List<ProductData> convertToProductDataList(
			List<ProductPojo> productPojoList,
			 List<BrandPojo> brandPojoList
			) throws ApiException {
		List<ProductData> dataList = new ArrayList<>();
		if(productPojoList.size() != brandPojoList.size()){
			throw new ApiException("Invalid pojo lists");
		}
		Integer len = productPojoList.size();
		for(Integer i=0; i < len; i++){
			ProductData productData = new ProductData();
			productData.setId(productPojoList.get(i).getId());
			productData.setBarcode(productPojoList.get(i).getBarcode());
			productData.setName(productPojoList.get(i).getName());
			productData.setBrand(brandPojoList.get(i).getBrand());
			productData.setCategory(brandPojoList.get(i).getCategory());
			productData.setBrandCategory(productPojoList.get(i).getBrandCategory());
			productData.setMrp(productPojoList.get(i).getMrp());
			dataList.add(productData);
		}
		return dataList;
	}

	public static ProductInventoryData convertToProductInventoryData(ProductPojo productPojo, BrandPojo brandPojo, InventoryPojo inventoryPojo){
		ProductInventoryData data = new ProductInventoryData();
		data.setBarcode(productPojo.getBarcode());
		data.setName(productPojo.getName());
		data.setBrand(brandPojo.getBrand());
		data.setCategory(brandPojo.getCategory());
		data.setMrp(productPojo.getMrp());
		data.setQuantity(inventoryPojo.getQuantity());
		return data;
	}
	public static ProductPojo convertToProductPojo(ProductForm f, Integer brandCategory) {
		ProductPojo p = new ProductPojo();
		p.setBarcode(f.getBarcode());
		p.setBrandCategory(brandCategory);
		logger.info(f.getMrp());
		p.setMrp(f.getMrp());
		p.setName(f.getName());
		return p;
	}
	
	public static InventoryPojo convertToInventoryPojo(InventoryForm f, Integer productId) throws ApiException{
		InventoryPojo p = new InventoryPojo();
		p.setProductId(productId);
		p.setQuantity(f.getQuantity());
		return p;
	}
	
	public static InventoryData convertToInventoryData(InventoryPojo p, ProductPojo productPojo) {
		InventoryData d = new InventoryData();
		d.setProductId(p.getProductId());
		d.setName(productPojo.getName());
		d.setBarcode(productPojo.getBarcode());
		d.setQuantity(p.getQuantity());
		return d;
	}

	public static List<InventoryData> convertToInventoryDataList
			(List<InventoryPojo> inventoryPojoList,
			 List<ProductPojo> productPojoList) throws ApiException {
		List<InventoryData> inventoryDataList = new ArrayList<InventoryData>();
		if(inventoryPojoList.size() != productPojoList.size()){
			return null;
		}
		Integer len = inventoryPojoList.size();
		for(Integer i=0; i < len; i++){
			InventoryData data = new InventoryData();
			data.setProductId(productPojoList.get(i).getId());
			data.setName(productPojoList.get(i).getName());
			data.setBarcode(productPojoList.get(i).getBarcode());
			data.setQuantity(inventoryPojoList.get(i).getQuantity());
			inventoryDataList.add(data);
		}
		return inventoryDataList;
	}

	private static OrderData convertToOrderData(OrderPojo orderPojo, Double totalAmount) {
		OrderData orderData = new OrderData();
		orderData.setId(orderPojo.getId());
		String format = "dd/MM/yyyy - HH:mm:ss";
		orderData.setTime(DateTimeUtil.getDateTimeString(orderPojo.getTime(), format));
		orderData.setTotalAmount(DoubleUtil.roundToString(totalAmount));
		orderData.setOrderStatus(orderPojo.getOrderStatus());
		return orderData;
	}

	public static List<OrderData> convertToOrderDataList(List<OrderPojo> orderPojoList, Map<Integer, List<OrderItemPojo>> integerListMap) throws ApiException {
		List<OrderData> orderDataList = new ArrayList<>();
		Double totalAmount = 0.0;
		for (OrderPojo orderPojo : orderPojoList) {
			totalAmount = 0.00;
			for(OrderItemPojo orderItemPojo: integerListMap.get(orderPojo.getId())){
				totalAmount += orderItemPojo.getSellingPrice() * orderItemPojo.getQuantity();
			}
			orderDataList.add(Converter.convertToOrderData(orderPojo, totalAmount));
		}
		return orderDataList;
	}


	public static OrderItemPojo convertToOrderItemPojo(OrderForm form,  Integer i, OrderPojo orderPojo, Integer productId) throws ApiException {
		OrderItemPojo orderItemPojo = new OrderItemPojo();
		orderItemPojo.setOrderId(orderPojo.getId());
		orderItemPojo.setProductId(productId);
		orderItemPojo.setQuantity(form.getQuantities().get(i));
		orderItemPojo.setSellingPrice(form.getSellingPrices().get(i));
		return orderItemPojo;
	}


	public static OrderItemData convertToOrderItemData(OrderItemPojo orderItemPojo, ProductPojo productPojo){
		OrderItemData orderItemData = new OrderItemData();
		orderItemData.setId(orderItemPojo.getId());
		orderItemData.setOrderId(orderItemPojo.getOrderId());
		orderItemData.setQuantity(orderItemPojo.getQuantity());
		orderItemData.setBarcode(productPojo.getBarcode());
		orderItemData.setProductName(productPojo.getName());
		orderItemData.setSellingPrice(DoubleUtil.roundToString(orderItemPojo.getSellingPrice()));
		orderItemData.setMrp(DoubleUtil.roundToString(productPojo.getMrp()));
		return orderItemData;

	}

	public static List<OrderItemData> convertToOrderItemDataList(List<OrderItemPojo> orderItemPojoList, List<ProductPojo> productPojoList) {
		if(orderItemPojoList.size() != productPojoList.size()){
			return null;
		}
		List<OrderItemData> orderItemDataList = new ArrayList<>();
		Integer len = orderItemPojoList.size();
		for(Integer i=0; i < len; i++){
			OrderItemData orderItemData = new OrderItemData();
			orderItemData.setId(orderItemPojoList.get(i).getId());
			orderItemData.setOrderId(orderItemPojoList.get(i).getOrderId());
			orderItemData.setBarcode(productPojoList.get(i).getBarcode());
			orderItemData.setProductName(productPojoList.get(i).getName());
			orderItemData.setQuantity(orderItemPojoList.get(i).getQuantity());
			orderItemData.setMrp(DoubleUtil.roundToString(productPojoList.get(i).getMrp()));
			orderItemData.setSellingPrice(DoubleUtil.roundToString(orderItemPojoList.get(i).getSellingPrice()));
			orderItemDataList.add(orderItemData);
		}
		return orderItemDataList;
	}

	public static InventoryReportData convertToInventoryReportData(InventoryPojo inventoryPojo, ProductPojo productPojo, BrandPojo brandPojo){
		InventoryReportData inventoryReportData = new InventoryReportData();
		inventoryReportData.setBrand(brandPojo.getBrand());
		inventoryReportData.setCategory(brandPojo.getCategory());
		inventoryReportData.setProductId(productPojo.getId());
		inventoryReportData.setProductName(productPojo.getName());
		inventoryReportData.setQuantity(inventoryPojo.getQuantity());
		inventoryReportData.setBarcode(productPojo.getBarcode());
		return inventoryReportData;
	}

	public static SalesReportData convertToSalesReportData(SalesReportData salesReportData, String key, Double value, Integer quantity){
		String[] brandCategoryArr = key.split("--");
		salesReportData.getBrands().add(brandCategoryArr[0]);
		salesReportData.getCategories().add(brandCategoryArr[1]);
		salesReportData.getQuantities().add(quantity);
		salesReportData.getTotalAmounts().add(DoubleUtil.roundToString(value));
		return salesReportData;
	}

	private static PosDaySalesData convertToPosDaySalesData(PosDaySalesPojo pojo){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		PosDaySalesData posDaySalesData = new PosDaySalesData();
		posDaySalesData.setDate(pojo.getDate().toLocalDate().format(dateTimeFormatter));
		posDaySalesData.setInvoicedOrdersCount(pojo.getInvoicedOrdersCount());
		posDaySalesData.setInvoicedItemsCount(pojo.getInvoicedItemsCount());
		posDaySalesData.setTotalRevenue(pojo.getTotalRevenue());
		return posDaySalesData;
	}

	public static List<PosDaySalesData> convertToPosDaySalesDataList(List<PosDaySalesPojo> pojos){
		List<PosDaySalesData> posDaySalesDataList = new ArrayList<>();
		for(PosDaySalesPojo p: pojos){
			posDaySalesDataList.add(Converter.convertToPosDaySalesData(p));
		}
		return posDaySalesDataList;
	}
}
