package com.increff.posapp.service;

import com.increff.posapp.dao.PosDaySalesDao;
import com.increff.posapp.pojo.PosDaySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class PosDaySalesService {

	@Autowired
	private PosDaySalesDao posDaySalesDao;

	public void add(PosDaySalesPojo p) throws ApiException {

		//Inserting
		posDaySalesDao.insert(p);
	}

	public ZonedDateTime getLastDateTime(){
		return posDaySalesDao.getLastDateTime();
	}

	public List<PosDaySalesPojo> getByInterval(ZonedDateTime startDate, ZonedDateTime endDate){
		return posDaySalesDao.selectByInterval(startDate, endDate);
	}
	public List<PosDaySalesPojo> getByInterval(ZonedDateTime startDate, ZonedDateTime endDate, Integer page, Integer size){
		return posDaySalesDao.selectByIntervalInPage(startDate, endDate, page, size);
	}

	public Long getByIntervalTotalElements(ZonedDateTime startDate, ZonedDateTime endDate){
		return posDaySalesDao.getByIntervalTotalElements(startDate, endDate);
	}

	public List<PosDaySalesPojo> getAll(){
		return posDaySalesDao.selectAll(PosDaySalesPojo.class);
	}

	public List<PosDaySalesPojo> getAll(Integer page, Integer size){
		return posDaySalesDao.selectAll(PosDaySalesPojo.class, page, size);
	}
}