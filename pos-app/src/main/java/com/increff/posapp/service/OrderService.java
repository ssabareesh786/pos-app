package com.increff.posapp.service;

import com.increff.posapp.dao.OrderDao;
import com.increff.posapp.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderService {

	@Autowired
	private OrderDao orderDao;

	public OrderPojo add(OrderPojo p) throws ApiException {
		validate(p);

		// Inserting the order
		return (OrderPojo) orderDao.insert(p);
	}

	public OrderPojo getById(Integer id) throws ApiException {
		return getCheckById(id);
	}

	public List<OrderPojo> getByInterval(ZonedDateTime startTime, ZonedDateTime endTime) throws ApiException {
		validateTimeInterval(startTime, endTime);
		return getCheckByInterval(startTime, endTime);
	}

	public List<OrderPojo> getAll() {
		return orderDao.selectAll(OrderPojo.class);
	}

	public List<OrderPojo> getAll(Integer page, Integer size){
		return orderDao.selectAll(OrderPojo.class, page, size);
	}
	public Long getTotalElements(){
		return orderDao.getTotalElements(OrderPojo.class);
	}
	public OrderPojo updateById(Integer id, OrderPojo p) throws ApiException {
		OrderPojo ex = getCheckById(id);
		ex.setTime(p.getTime());
		orderDao.update(ex);
		return ex;
	}

	public Integer deleteById(Integer id) throws ApiException {
		getCheckById(id);
		return orderDao.delete(id);
	}

	private OrderPojo getCheckById(Integer id) throws ApiException {
		OrderPojo p = orderDao.selectById(id);
		if (p == null) {
			throw new ApiException("Order with the given id doesn't exist");
		}
		return p;
	}

	private List<OrderPojo> getCheckByInterval(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
		return orderDao.selectByIntervalInvoiced(startDate, endDate);
	}
	private void validate(OrderPojo p) throws ApiException {
		if(p.getTime() == null) {
			throw new ApiException("Date and Time cannot be null");
		}
	}

	private void validateTimeInterval(ZonedDateTime startTime, ZonedDateTime endTime) throws ApiException {
		if(endTime.toLocalDate().isAfter(LocalDate.now())){
			throw new ApiException("End date should be today's date or a date before today");
		}
		if(ChronoUnit.DAYS.between(startTime.toLocalDate(), endTime.toLocalDate()) > 366){
			throw new ApiException("Difference between the two entered dates must be within one year");
		}
	}
}
