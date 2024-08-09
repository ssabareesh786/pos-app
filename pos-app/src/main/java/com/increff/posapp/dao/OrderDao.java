package com.increff.posapp.dao;

import com.increff.posapp.pojo.OrderPojo;
import com.increff.posapp.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Transactional(rollbackOn = ApiException.class)
public class OrderDao extends AbstractDao{

	private static final String SELECT_BY_ID = "select p from OrderPojo p where id=:id";
	private static final String SELECT_BY_INTERVAL_INVOICED = "select p from OrderPojo p where time >= :startTime and time <=:endTime and orderStatus='INVOICED'";
	private static final String DELETE_BY_ID = "delete from OrderPojo p where id=:id";
	public OrderPojo selectById(Integer id) {
		TypedQuery<OrderPojo> query = getQuery(SELECT_BY_ID, OrderPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public List<OrderPojo> selectByIntervalInvoiced(ZonedDateTime startTime, ZonedDateTime endTime){
		TypedQuery<OrderPojo> query = getQuery(SELECT_BY_INTERVAL_INVOICED, OrderPojo.class);
		query.setParameter("startTime", startTime);
		query.setParameter("endTime", endTime);
		return query.getResultList();
	}

	public void update(OrderPojo p) {
		// Implemented by Spring itself
	}

	public Integer delete(Integer id){
		Query query = em().createQuery(DELETE_BY_ID);
		query.setParameter("id", id);
		return query.executeUpdate();
	}
}
