package com.increff.posapp.dto;

import com.increff.posapp.model.ProductData;
import com.increff.posapp.model.ProductForm;
import com.increff.posapp.model.ProductInventoryData;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.pojo.InventoryPojo;
import com.increff.posapp.pojo.ProductPojo;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.BrandService;
import com.increff.posapp.service.InventoryService;
import com.increff.posapp.service.ProductService;
import com.increff.posapp.util.Converter;
import com.increff.posapp.util.Normalizer;
import com.increff.posapp.util.StringUtil;
import com.increff.posapp.util.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDto {
	@Autowired
	private ProductService productService;
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private BrandService brandService;
	private Logger logger = Logger.getLogger(ProductDto.class);

	public ProductData add(ProductForm form) throws ApiException{
		Validator.validate(form);
		Validator.validateBarcode(form.getBarcode());
		Normalizer.normalize(form);
		BrandPojo brandPojo = brandService.getByBrandAndCategory(form.getBrand(), form.getCategory());
		Integer brandCategory = brandPojo.getId();
		ProductPojo productPojo = Converter.convertToProductPojo(form, brandCategory);
		ProductPojo pojo = productService.add(productPojo);
		return Converter.convertToProductData(pojo, brandPojo);
	}

	public ProductData get(Integer id) throws ApiException {
		Validator.validate("Id", id);
		ProductPojo productPojo = productService.getById(id);
		BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
		return Converter.convertToProductData(productPojo, brandPojo);
	}

	public Object get(String barcode, Boolean inventoryStatus, Integer page, Integer size) throws ApiException {
		if(barcode == null && page != null && size != null){
			return getAll(page, size);
		}
		Validator.validateBarcode(barcode);
		if(barcode != null && inventoryStatus){
			return getDataAndInventoryStatus(barcode);
		}
		if(barcode != null){
			return getData(barcode);
		}
		throw new ApiException("Invalid request");
	}

	public ProductData update(Integer id, ProductForm form) throws ApiException {
		Validator.validate("Id", id);
		Validator.validate(form);
		Validator.validateBarcode(form.getBarcode());
		Normalizer.normalize(form);
		BrandPojo brandPojo = brandService.getByBrandAndCategory(form.getBrand(), form.getCategory());
		ProductPojo productPojo = Converter.convertToProductPojo(form, brandPojo.getId());
		productPojo.setId(id);
		return Converter.convertToProductData(productService.updateById(id, productPojo), brandPojo);
	}

	private ProductInventoryData getDataAndInventoryStatus(String barcode) throws ApiException {
		Validator.validate("Barcode", barcode);
		Validator.validateBarcode(barcode);
		barcode = StringUtil.toLowerCase(barcode);
		ProductPojo productPojo = productService.getByBarcode(barcode);
		BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
		InventoryPojo inventoryPojo = inventoryService.get(productPojo.getId());
		return Converter.convertToProductInventoryData(productPojo, brandPojo, inventoryPojo);
	}

	private ProductData getData(String barcode) throws ApiException {
		Validator.validate("Barcode", barcode);
		Validator.validateBarcode(barcode);
		barcode = StringUtil.toLowerCase(barcode);
		ProductPojo productPojo = productService.getByBarcode(barcode);
		BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
		return Converter.convertToProductData(productPojo, brandPojo);
	}
	
	private Page<ProductData> getAll(Integer page, Integer size) throws ApiException{
		Validator.validate("Page",page);
		Validator.validate("Size", size);
		List<ProductPojo> productPojoList =  productService.getAll(page, size);
		List<BrandPojo> brandPojoList = new ArrayList<>();
		List<InventoryPojo> inventoryPojoList = new ArrayList<>();
		for (ProductPojo productPojo : productPojoList) {
			BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
			brandPojoList.add(brandPojo);
		}
		List<ProductData> productDataList = Converter.convertToProductDataList(productPojoList, brandPojoList);
		return new PageImpl<>(productDataList, PageRequest.of(page, size), productService.getTotalElements());
	}
}
