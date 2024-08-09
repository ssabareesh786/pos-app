package com.increff.posapp.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.increff.posapp.dao.OrderItemDao;
import com.increff.posapp.pojo.OrderItemPojo;
import com.increff.posapp.util.DoubleUtil;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderItemService {

	@Autowired
	private OrderItemDao orderItemDao;

	public OrderItemPojo add(OrderItemPojo p) throws ApiException {
		normalize(p);
		validate(p);
		return (OrderItemPojo) orderItemDao.insert(p);
	}

	public OrderItemPojo getById(Integer id) throws ApiException {
		return getCheckById(id);
	}

	   
	public List<OrderItemPojo> getByOrderId(Integer orderId) throws ApiException {
		return getCheckByOrderId(orderId);
	}

	public OrderItemPojo getOrderIdAndProductId(Integer orderId, Integer productId){
		return orderItemDao.selectByOrderIdAndProductId(orderId, productId);
	}

	public List<OrderItemPojo> getPageByOrderId(Integer orderId, Integer page, Integer size) throws ApiException {
		getCheckByOrderId(orderId);
		return orderItemDao.getPageByOrderId(orderId, page, size);
	}
	public Long getByOrderIdTotalElements(Integer orderId){
		return orderItemDao.getByOrderIdTotalElements(orderId);
	}

	public Long getTotalInvoicedQuantity(Integer orderId){
		return orderItemDao.getInvoicedQuantityByOrderId(orderId);
	}

	public Double getTotalCost(Integer orderId){
		return orderItemDao.getTotalCostByOrderId(orderId);
	}

	// For update

	public OrderItemPojo updateById(Integer id, OrderItemPojo p) throws ApiException {
		normalize(p);
		validate(p);
		OrderItemPojo ex = getCheckById(id);
		ex.setOrderId(p.getOrderId());
		ex.setProductId(p.getProductId());
		ex.setQuantity(p.getQuantity());
		ex.setSellingPrice(p.getSellingPrice());
		orderItemDao.update(ex);
		return ex;
	}

	public Integer deleteById(Integer id) throws ApiException {
		getCheckById(id);
		return orderItemDao.deleteById(id);
	}
	   
	private OrderItemPojo getCheckById(Integer id) throws ApiException {
		OrderItemPojo p = orderItemDao.selectById(id);
		if (p == null) {
			throw new ApiException("Item with the given id doesn't exist");
		}
		return p;
	}

	private List<OrderItemPojo> getCheckByOrderId(Integer orderId) throws ApiException {
		List<OrderItemPojo> list = orderItemDao.selectByOrderId(orderId);
		if (list.isEmpty()) {
			throw new ApiException("Order Id doesn't exist");
		}
		return list;
	}

	private static void normalize(OrderItemPojo p) {
		p.setSellingPrice(DoubleUtil.round(p.getSellingPrice(), 2));
	}
	private void validate(OrderItemPojo p) throws ApiException {
		if(p.getOrderId() == null){
			throw new ApiException("Order Id can't be empty");
		}
		if(p.getProductId() == null){
			throw new ApiException("Order Id can't be empty");
		}
		if(p.getQuantity() == null){
			throw new ApiException("Order Id can't be empty");
		}
		if(p.getSellingPrice() == null){
			throw new ApiException("Selling Price can't be empty");
		}
		if(p.getSellingPrice() <= 0.0){
			throw new ApiException("Selling price must be greater than zero");
		}
		if(p.getQuantity() <= 0){
			throw new ApiException("Quantity must be greater than zero");
		}
		if(p.getSellingPrice().isInfinite() || p.getSellingPrice().isNaN()){
			throw new ApiException("Selling price is not valid");
		}
	}
}
