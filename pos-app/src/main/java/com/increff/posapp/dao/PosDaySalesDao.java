package com.increff.posapp.dao;

import com.increff.posapp.pojo.PosDaySalesPojo;
import com.increff.posapp.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Transactional(rollbackOn = ApiException.class)
public class PosDaySalesDao extends AbstractDao{

	private static final String SELECT_BY_INTERVAL = "select p from PosDaySalesPojo p where date >= :startDate and date <= :endDate";
	private static final String SELECT_LAST_DATE_TIME = "select max(date) from PosDaySalesPojo p";
	private static final String SELECT_INTERVAL_TOTAL_COUNT = "select count(p) from PosDaySalesPojo p where date >= :startDate and date <= :endDate";

	public void insert(PosDaySalesPojo p) {
		em().persist(p);
	}

	public List<PosDaySalesPojo> selectByInterval(ZonedDateTime startDate, ZonedDateTime endDate){
		TypedQuery<PosDaySalesPojo> query = getQuery(SELECT_BY_INTERVAL, PosDaySalesPojo.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.getResultList();
	}

	public List<PosDaySalesPojo> selectByIntervalInPage(ZonedDateTime startDate, ZonedDateTime endDate, Integer page, Integer size){
		TypedQuery<PosDaySalesPojo> query = getQuery(SELECT_BY_INTERVAL, PosDaySalesPojo.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setFirstResult(page*size);
		query.setMaxResults(size);
		return query.getResultList();
	}

	public ZonedDateTime getLastDateTime(){
		return em().createQuery(SELECT_LAST_DATE_TIME, ZonedDateTime.class).getSingleResult();
	}

	public Long getByIntervalTotalElements(ZonedDateTime startDate, ZonedDateTime endDate){
		return em()
				.createQuery(SELECT_INTERVAL_TOTAL_COUNT, Long.class)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getSingleResult();
	}

}
