package com.increff.posapp.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.posapp.service.ApiException;
import org.springframework.stereotype.Repository;

import com.increff.posapp.pojo.UserPojo;

@Repository
@Transactional(rollbackOn = ApiException.class)
public class UserDao extends AbstractDao {

	private static final String DELETE_BY_ID = "delete from UserPojo p where id=:id";
	private static final String SELECT_BY_ID = "select p from UserPojo p where id=:id";
	private static final String SELECT_BY_EMAIL = "select p from UserPojo p where email=:email";

	public Integer delete(Integer id) {
		Query query = em().createQuery(DELETE_BY_ID);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public UserPojo select(Integer id) {
		TypedQuery<UserPojo> query = getQuery(SELECT_BY_ID, UserPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public UserPojo select(String email) {
		TypedQuery<UserPojo> query = getQuery(SELECT_BY_EMAIL, UserPojo.class);
		query.setParameter("email", email);
		return getSingle(query);
	}

	public void update(UserPojo p) {
		//Implemented by Spring itself
	}


}
