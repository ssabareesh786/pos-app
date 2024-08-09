package com.increff.posapp.service;

import java.util.List;

import javax.transaction.Transactional;

import com.increff.posapp.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.posapp.dao.UserDao;
import com.increff.posapp.pojo.UserPojo;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserService {

	@Autowired
	private UserDao dao;

	public UserPojo add(UserPojo p) throws ApiException {
		normalize(p);
		validate(p);
		UserPojo existing = dao.select(p.getEmail());
		if (existing != null) {
			throw new ApiException("User with given email already exists");
		}
		return (UserPojo) dao.insert(p);
	}

	public UserPojo get(String email) throws ApiException {
		return dao.select(email);
	}

	public UserPojo get(Integer id) throws ApiException {
		return getCheckById(id);
	}

	public List<UserPojo> getAllInPage(Integer page, Integer size) {
		return dao.selectAll(UserPojo.class, page, size);
	}

	public Long getTotalUsers(){
		return dao.getTotalElements(UserPojo.class);
	}

	public void delete(int id) throws ApiException {
		getCheckById(id);
		dao.delete(id);
	}

	private UserPojo getCheckById(Integer id) throws ApiException {
		UserPojo pojo = dao.select(id);
		if(pojo == null){
			throw new ApiException("No user with the given id");
		}
		return pojo;
	}

	public UserPojo update(Integer id, UserPojo p) throws ApiException {
		UserPojo ex = getCheckById(id);
		ex.setEmail(p.getEmail());
		ex.setPassword(p.getPassword());
		ex.setRole(p.getRole());
		dao.update(ex);
		return ex;
	}
	private static void normalize(UserPojo p) {
		p.setEmail(StringUtil.toLowerCase(p.getEmail()));
		p.setRole(StringUtil.toLowerCase(p.getRole()));
	}

	private void validate(UserPojo p) throws ApiException {
		if(p.getEmail() == null){
			throw new ApiException("Email can't be empty");
		}
		if(p.getPassword() == null){
			throw new ApiException("Password can't be empty");
		}
	}
}
