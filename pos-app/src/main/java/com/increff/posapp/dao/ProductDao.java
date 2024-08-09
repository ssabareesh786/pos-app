package com.increff.posapp.dao;

import com.increff.posapp.pojo.ProductPojo;
import com.increff.posapp.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
@Transactional(rollbackOn = ApiException.class)
public class ProductDao extends AbstractDao{
	private static final String SELECT_BY_ID = "select p from ProductPojo p where id=:id";
	private static final String SELECT_BY_BARCODE = "select p from ProductPojo p where barcode=:barcode";

	public ProductPojo selectById(Integer id) {
		TypedQuery<ProductPojo> query = getQuery(SELECT_BY_ID, ProductPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}
	
	public ProductPojo selectByBarcode(String barcode) {
		TypedQuery<ProductPojo> query = getQuery(SELECT_BY_BARCODE, ProductPojo.class);
		query.setParameter("barcode", barcode);
		return getSingle(query);
	}

	public void update(ProductPojo productPojo) {
		// Implemented by Spring itself
	}
}
