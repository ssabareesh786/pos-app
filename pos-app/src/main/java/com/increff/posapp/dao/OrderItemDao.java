package com.increff.posapp.dao;

import com.increff.posapp.pojo.OrderItemPojo;
import com.increff.posapp.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(rollbackOn = ApiException.class)
public class OrderItemDao extends AbstractDao{

	private static final String SELECT_BY_ID = "select p from OrderItemPojo p where id=:id";
	private static final String SELECT_BY_ORDER_ID = "select p from OrderItemPojo p where orderId=:orderId";
	private static final String SUM_OF_INVOICED_QUANTITY_BY_ORDER_ID = "select sum(quantity) from OrderItemPojo p where orderId=:orderId";
	private static final String TOTAL_COST_BY_ORDER_ID = "select sum(quantity*sellingPrice) from OrderItemPojo p where orderId=:orderId";
	private static final String SELECT_BY_ORDER_ID_COUNT = "select count(p) from OrderItemPojo p where orderId=:orderId";
	private static final String SELECT_BY_ORDER_ID_AND_PRODUCT_ID = "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";
	private static final String DELETE_BY_ID = "delete from OrderItemPojo p where id=:id";
	public OrderItemPojo selectById(Integer id) {
		TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ID, OrderItemPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public List<OrderItemPojo> selectByOrderId(Integer orderId) {
		TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ORDER_ID, OrderItemPojo.class);
		query.setParameter("orderId", orderId);
		return query.getResultList();
	}

	public OrderItemPojo selectByOrderIdAndProductId(Integer orderId, Integer productId){
		TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ORDER_ID_AND_PRODUCT_ID, OrderItemPojo.class);
		query.setParameter("orderId", orderId);
		query.setParameter("productId", productId);
		return getSingle(query);
	}

	public List<OrderItemPojo> getPageByOrderId(Integer orderId, Integer page, Integer size) {
		TypedQuery<OrderItemPojo> query = getQuery(SELECT_BY_ORDER_ID, OrderItemPojo.class);
		query.setParameter("orderId", orderId);
		query.setFirstResult(page*size);
		query.setMaxResults(size);

		return query.getResultList();
	}

	public Long getByOrderIdTotalElements(Integer orderId){
		return em()
				.createQuery(SELECT_BY_ORDER_ID_COUNT, Long.class)
				.setParameter("orderId", orderId)
				.getSingleResult();
	}

	public Long getInvoicedQuantityByOrderId(Integer orderId){
		return em().createQuery(SUM_OF_INVOICED_QUANTITY_BY_ORDER_ID, Long.class).setParameter("orderId", orderId).getSingleResult();
	}

	public Double getTotalCostByOrderId(Integer orderId){
		return em().createQuery(TOTAL_COST_BY_ORDER_ID, Double.class).setParameter("orderId", orderId).getSingleResult();
	}

	public void update(OrderItemPojo p) {
		// Implemented by Spring itself
	}

	public Integer deleteById(Integer id){
		Query query = em().createQuery(DELETE_BY_ID);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

}
