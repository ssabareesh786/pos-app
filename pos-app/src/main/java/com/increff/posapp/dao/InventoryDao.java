package com.increff.posapp.dao;

import com.increff.posapp.pojo.InventoryPojo;
import com.increff.posapp.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Repository
@Transactional(rollbackOn = ApiException.class)
public class InventoryDao extends AbstractDao{

	private static final String SELECT_BY_PRODUCT_ID = "select p from InventoryPojo p where productId=:productId";

	public InventoryPojo selectByProductId(Integer pid) {
		TypedQuery<InventoryPojo> query = getQuery(SELECT_BY_PRODUCT_ID, InventoryPojo.class);
		query.setParameter("productId", pid);
		return getSingle(query);
	}

	public void update(InventoryPojo inventoryPojo) {
		// Implemented by Spring itself
	}

}
