package com.increff.posapp.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.posapp.dao.ProductDao;
import com.increff.posapp.pojo.ProductPojo;
import com.increff.posapp.util.DoubleUtil;
import com.increff.posapp.util.StringUtil;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService {

	@Autowired
	private ProductDao productDao;

	public ProductPojo add(ProductPojo p) throws ApiException {
		normalize(p);
		validate(p);
		validateBarcode(p);
		//Inserting
		return (ProductPojo) productDao.insert(p);
	}

	public ProductPojo getById(int id) throws ApiException {
		return getCheckById(id);
	}

	public ProductPojo getByBarcode(String barcode) throws ApiException {
		return getCheckByBarcode(barcode);
	}

	public List<ProductPojo> getAll() {
		return productDao.selectAll(ProductPojo.class);
	}

	public List<ProductPojo> getAll(Integer page, Integer size){
		return productDao.selectAll(ProductPojo.class, page, size);
	}
	public Long getTotalElements(){
		return productDao.getTotalElements(ProductPojo.class);
	}
	public ProductPojo updateById(int id, ProductPojo p) throws ApiException {
		normalize(p);
		validate(p);
		ProductPojo ex = getCheckById(id);
		ex.setBarcode(p.getBarcode());
		ex.setBrandCategory(p.getBrandCategory());
		ex.setName(p.getName());
		ex.setMrp(p.getMrp());
		productDao.update(ex);
		return ex;
	}

	// Private methods

	private ProductPojo getCheckById(Integer id) throws ApiException {
		ProductPojo p = productDao.selectById(id);
		if (p == null) {
			throw new ApiException("Product with given ID does not exist");
		}
		return p;
	}

	private ProductPojo getCheckByBarcode(String barcode) throws ApiException {
		ProductPojo p = productDao.selectByBarcode(barcode);
		if (p == null) {
			throw new ApiException("Product with given barcode doesn't exist");
		}
		return p;
	}

	private void normalize(ProductPojo p) {
		p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
		p.setName(StringUtil.toLowerCase(p.getName()));
		p.setMrp(DoubleUtil.round(p.getMrp(), 2));
	}

	private void validate(ProductPojo p) throws ApiException {
		if(StringUtil.isEmpty(p.getBarcode())){
			throw new ApiException("Barcode can't be empty");
		}
		if(StringUtil.isEmpty(p.getName())){
			throw new ApiException("Product name can't be empty");
		}
		if(StringUtil.isNotAlNum(p.getBarcode()) || StringUtil.isNotAlNum(p.getName())){
				throw new ApiException("Characters other than alpha-numeric is not allowed");
		}
		if(p.getBrandCategory() == null){
			throw new ApiException("Brand-Category number can't be empty");
		}
		if(p.getMrp() == null){
			throw new ApiException("MRP can't be empty");
		}
		if(p.getMrp().isInfinite() || p.getMrp().isNaN()) {
			throw new ApiException("MRP is invalid");
		}
		if(p.getMrp() <= 0.0){
			throw new ApiException("MRP must be greater than zero");
		}
	}
	private void validateBarcode(ProductPojo p) throws ApiException {
		if(productDao.selectByBarcode(p.getBarcode()) != null){
			throw new ApiException("The entered barcode already exists");
		}
	}
}