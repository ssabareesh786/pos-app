package com.increff.posapp.dto;

import com.increff.posapp.model.InventoryData;
import com.increff.posapp.model.InventoryForm;
import com.increff.posapp.pojo.InventoryPojo;
import com.increff.posapp.pojo.ProductPojo;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.InventoryService;
import com.increff.posapp.service.ProductService;
import com.increff.posapp.util.Converter;
import com.increff.posapp.util.Normalizer;
import com.increff.posapp.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryDto {
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private ProductService productService;
	
	public InventoryData add(InventoryForm form) throws ApiException{
		Validator.validate(form);
		Validator.validateBarcode(form.getBarcode());
		Normalizer.normalize(form);
		ProductPojo productPojo = productService.getByBarcode(form.getBarcode());
		Integer productId = productPojo.getId();
		InventoryPojo inventoryPojo = Converter.convertToInventoryPojo(form, productId);
		return Converter.convertToInventoryData(inventoryService.add(inventoryPojo), productPojo);
	}

	public InventoryData get(Integer productId) throws ApiException {
		Validator.validate("Product id", productId);
		InventoryPojo inventoryPojo = inventoryService.get(productId);
		ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
		return Converter.convertToInventoryData(inventoryPojo, productPojo);
	}

	public Page<InventoryData> get(Integer page, Integer size) throws ApiException{
		Validator.validate("Page", page);
		Validator.validate("Size", size);
		List<InventoryPojo> inventoryPojoList = inventoryService.getAll(page, size);
		List<ProductPojo> productPojoList = new ArrayList<>();
		for(InventoryPojo inventoryPojo: inventoryPojoList) {
			productPojoList.add(productService.getById(inventoryPojo.getProductId()));
		}
		List<InventoryData> inventoryDataList = Converter.convertToInventoryDataList(
				inventoryPojoList,
				productPojoList);
		return new PageImpl<>(inventoryDataList, PageRequest.of(page, size), inventoryService.getTotalElements());
	}
	
	public InventoryData update(
			Integer id,
			InventoryForm form)
			throws ApiException{
		Validator.validate("Id", id);
		Validator.validate(form);
		Validator.validateBarcode(form.getBarcode());
		Normalizer.normalize(form);
		ProductPojo productPojo = productService.getByBarcode(form.getBarcode());
		Integer productId = productPojo.getId();
		InventoryPojo inventoryPojo = Converter.convertToInventoryPojo(form, productId);
		return Converter.convertToInventoryData(
				inventoryService.update(inventoryPojo),
				productPojo
		);
	}
}
