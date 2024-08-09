package com.increff.posapp.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.posapp.model.BrandData;
import com.increff.posapp.util.Converter;
import com.increff.posapp.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.posapp.dao.BrandDao;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.util.StringUtil;
@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandService {

	@Autowired
	private BrandDao dao;

	public BrandPojo add(BrandPojo p) throws ApiException {
		normalize(p);
		validate(p);
		return (BrandPojo) dao.insert(p);
	}

	public BrandPojo getById(int id) throws ApiException {
		return getCheckById(id);
	}

	public List<BrandPojo> getByBrand(String brand) throws ApiException {
		brand = validateAndNormalizeString(brand, "Brand");
		return getCheckByBrand(brand);
	}

	public List<BrandPojo> getByCategory(String category) throws ApiException {
		category = validateAndNormalizeString(category, "Category");
		return getCheckByCategory(category);
	}

	public List<BrandPojo> getByBrand(String brand, Integer page, Integer size) throws ApiException {
		brand = validateAndNormalizeString(brand, "Brand");
		Validator.validate("Page", page);
		Validator.validate("Size", size);
		return getCheckByBrand(brand, page, size);
	}
	public Long getByBrandTotalElements(String brand) throws ApiException {
		brand = validateAndNormalizeString(brand, "Brand");
		return dao.getBrandTotalElements(brand);
	}

	public List<BrandPojo> getByCategory(String category, Integer page, Integer size) throws ApiException {
		category = validateAndNormalizeString(category, "Category");
		Validator.validate("Page",page);
		Validator.validate("Size", size);
		return getCheckByCategory(category, page, size);
	}
	public Long getCategoryTotalElements(String category) throws ApiException {
		category = validateAndNormalizeString(category, "Category");
		return dao.getCategoryTotalElements(category);
	}

	public BrandPojo getByBrandAndCategory(String brand, String category) throws ApiException {
		brand = validateAndNormalizeString(brand, "Brand");
		category = validateAndNormalizeString(category, "Category");
		return getCheckByBrandAndCategory(brand, category);
	}

	public List<BrandPojo> getAll() throws ApiException {
		return dao.selectAll(BrandPojo.class);
	}
	public List<BrandPojo> getAll(Integer page, Integer size) throws ApiException {
		Validator.validate("Page",page);
		Validator.validate("Size", size);
		return dao.selectAll(BrandPojo.class, page, size);
	}
	public Long getTotalElements(){
		return dao.getTotalElements(BrandPojo.class);
	}
	public BrandPojo update(int id, BrandPojo p) throws ApiException {
		normalize(p);
		validate(p);
		BrandPojo ex = getCheckById(id);
		ex.setBrand(p.getBrand());
		ex.setCategory(p.getCategory());
		dao.update(ex);
		return ex;
	}


	// Private methods
	private BrandPojo getCheckById(int id) throws ApiException {
		BrandPojo p = dao.selectById(id);
		if (p == null) {
			throw new ApiException("Brand Category combination with given ID does not exit");
		}
		return p;
	}

	private List<BrandPojo> getCheckByBrand(String brand, Integer page, Integer size) throws ApiException {
		List<BrandPojo> list = dao.selectByBrand(brand, page, size);
		if (list.size() == 0) {
			throw new ApiException("The given brand doesn't exist");
		}
		return list;
	}

	private List<BrandPojo> getCheckByBrand(String brand) throws ApiException {
		List<BrandPojo> pojos = dao.selectByBrand(brand);
		if(pojos.size() == 0){
			throw new ApiException("No such brand");
		}
		return pojos;
	}

	private List<BrandPojo> getCheckByCategory(String category, Integer page, Integer size) throws ApiException {
		List<BrandPojo> list = dao.selectByCategory(category, page, size);
		if (list.size() == 0) {
			throw new ApiException("The given category doesn't exist");
		}
		return list;
	}

	private List<BrandPojo> getCheckByCategory(String category) throws ApiException {
		List<BrandPojo> list = dao.selectByCategory(category);
		if(list.size() == 0){
			throw new ApiException("No such category");
		}
		return list;
	}

	private BrandPojo getCheckByBrandAndCategory(String brand, String category) throws ApiException {
		BrandPojo p = dao.selectByBrandAndCategory(brand, category);
		if (p == null) {
			throw new ApiException("The given brand and category combination doesn't exist");
		}
		return p;
	}
	
	private void normalize(BrandPojo p) {
		p.setBrand(StringUtil.toLowerCase(p.getBrand()));
		p.setCategory(StringUtil.toLowerCase(p.getCategory()));
	}

	private void validate(BrandPojo p) throws ApiException {
		if(StringUtil.isEmpty(p.getBrand())){
			throw new ApiException("Brand can't be empty");
		}

		if(StringUtil.isEmpty(p.getCategory())){
			throw new ApiException("Category can't be empty");
		}
		if(StringUtil.isNotAlNum(p.getBrand()) || StringUtil.isNotAlNum(p.getCategory())){
			throw new ApiException("Characters other than alpha-numeric is not allowed");
		}
		if(dao.selectByBrandAndCategory(p.getBrand(), p.getCategory()) != null) {
			throw new ApiException("The entered brand and category combination already exists");
		}
	}

	private String validateAndNormalizeString(String s, String field) throws ApiException {
		s = StringUtil.toLowerCase(s);
		if(s == null || s.length() == 0){
			throw new ApiException(field + " is empty");
		}
		return s;
	}
}
