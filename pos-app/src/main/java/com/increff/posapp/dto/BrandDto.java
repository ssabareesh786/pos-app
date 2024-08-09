package com.increff.posapp.dto;

import java.util.List;

import com.increff.posapp.util.Converter;
import com.increff.posapp.util.Normalizer;
import com.increff.posapp.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.increff.posapp.model.BrandData;
import com.increff.posapp.model.BrandForm;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.BrandService;

@Component
public class BrandDto {
	@Autowired
	private BrandService brandService;
	
	public BrandData add(BrandForm form) throws ApiException {
		Validator.validate(form);
		Normalizer.normalize(form);
		BrandPojo brandPojo = Converter.convertToBrandPojo(form);
		return Converter.convertToBrandData(brandService.add(brandPojo));
	}

	public BrandData update(Integer id, BrandForm form) throws ApiException{
		Validator.validate("Id", id);
		Validator.validate(form);
		Normalizer.normalize(form);
		BrandPojo p = Converter.convertToBrandPojo(form);
		return Converter.convertToBrandData(brandService.update(id, p));
	}

	public BrandData get(Integer id) throws ApiException{
		Validator.validate("Id", id);
		Validator.validate(id);
		return Converter.convertToBrandData(brandService.getById(id));
	}

	public Page<BrandData> get(Integer page, Integer size) throws ApiException {
		Validator.validate("Page", page);
		Validator.validate("Size", size);
		List<BrandPojo> brandPojoList = brandService.getAll(page, size);
		List<BrandData> listBrandData = Converter.convertToBrandDataList(brandPojoList);
		return new PageImpl<>(listBrandData, PageRequest.of(page, size), brandService.getTotalElements());
	}
}
