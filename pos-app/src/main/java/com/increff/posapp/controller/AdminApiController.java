package com.increff.posapp.controller;

import java.util.*;

import com.increff.posapp.model.UserEditForm;
import com.increff.posapp.util.StringUtil;
import com.increff.posapp.util.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.increff.posapp.model.UserData;
import com.increff.posapp.model.UserForm;
import com.increff.posapp.pojo.UserPojo;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/api/supervisor")
public class AdminApiController {

	@Autowired
	private UserService service;
	@Value("${app.supervisors}")
	private String emails;
	private static Logger logger = Logger.getLogger(AdminApiController.class);

	@ApiOperation(value = "Adds a user")
	@RequestMapping(path = "/user", method = RequestMethod.POST)
	public void addUser(@RequestBody UserForm form) throws ApiException {
		Validator.validateEmail(form.getEmail());
		Validator.validatePassword(form.getPassword());
		String[] emailArray = emails.split(",");
		Set<String> emailSet = new HashSet<>(Arrays.asList(emailArray));
		UserPojo p = convert(form);
		if(emailSet.contains(form.getEmail()))
			p.setRole("supervisor");
		else
			p.setRole("operator");
		service.add(p);
	}

	@ApiOperation(value = "Deletes a user")
	@RequestMapping(path = "/user/{id}", method = RequestMethod.DELETE)
	public void deleteUser(@PathVariable int id) throws ApiException {
		service.delete(id);
	}

	@ApiOperation(value = "Deletes a user")
	@RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
	public UserData getUser(@PathVariable int id) throws ApiException {
		return convert(service.get(id));
	}

	@ApiOperation(value = "Gets list of all users")
	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public Page<UserData> getAllUser(@RequestParam(name = "page-number") Integer page, @RequestParam(name = "page-size") Integer size) {
		List<UserPojo> list = service.getAllInPage(page, size);
		List<UserData> list2 = new ArrayList<UserData>();
		for (UserPojo p : list) {
			list2.add(convert(p));
		}
		return new PageImpl<>(list2, PageRequest.of(page, size), service.getTotalUsers());
	}

	@ApiOperation(value = "Deletes a user")
	@RequestMapping(path = "/user/{id}", method = RequestMethod.PUT)
	public void updateUser(@PathVariable int id, @RequestBody UserEditForm form) throws ApiException {
		Validator.validateEmail(form.getEmail());
		form.setEmail(StringUtil.toLowerCase(form.getEmail()));
		Validator.validate("Role", form.getRole());
		Validator.validateRole(form.getRole());
		form.setRole(StringUtil.toLowerCase(form.getRole()));
		UserPojo pojo = service.get(id);
		pojo.setEmail(form.getEmail());
		pojo.setRole(form.getRole());
		if(!StringUtil.isEmpty(form.getPassword())){
			Validator.validatePassword(form.getPassword());
		}
		pojo.setPassword(form.getPassword());
		service.update(id, pojo);
	}

	private static UserData convert(UserPojo p) {
		UserData d = new UserData();
		d.setEmail(p.getEmail());
		d.setRole(p.getRole());
		d.setId(p.getId());
		return d;
	}

	private static UserPojo convert(UserForm f) {
		UserPojo p = new UserPojo();
		p.setEmail(f.getEmail());
		p.setPassword(f.getPassword());
		return p;
	}
}
