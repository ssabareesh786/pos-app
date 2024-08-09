package com.increff.posapp.controller;

import com.increff.posapp.model.InfoData;
import com.increff.posapp.model.SignUpForm;
import com.increff.posapp.model.UserData;
import com.increff.posapp.pojo.UserPojo;
import com.increff.posapp.service.ApiException;
import com.increff.posapp.service.UserService;
import com.increff.posapp.util.SecurityUtil;
import com.increff.posapp.util.StringUtil;
import com.increff.posapp.util.UserPrincipal;
import com.increff.posapp.util.Validator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Api
@RestController
public class SignUpController {

	@Autowired
	private UserService service;
	@Autowired
	private InfoData info;
	@Value("${app.supervisors}")
	private String emails;
	private static Logger logger = Logger.getLogger(SignUpController.class);

	@ApiOperation(value = "User is getting added")
	@RequestMapping(path = "/session/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ModelAndView signUp(HttpServletRequest req, SignUpForm form) throws ApiException {
		try{
			Validator.validateEmail(form.getEmail());
		}
		catch (ApiException ex){
			info.setMessage(ex.getMessage());
			info.setPageType("Sign Up");
			return new ModelAndView("redirect:/site/signup");
		}
		form.setEmail(StringUtil.toLowerCase(form.getEmail()));
		String[] emailArray = emails.split(",");
		Set<String> emailSet = new HashSet<>(Arrays.asList(emailArray));
		UserPojo p = convertToUserPojo(form);
		boolean authenticated = (service.get(p.getEmail()) == null );
		if (!authenticated) {
			info.setMessage("Email Id already exists");
			info.setPageType("Sign Up");
			return new ModelAndView("redirect:/site/signup");
		}
		try {
			Validator.validatePassword(form.getPassword());
		}
		catch (ApiException ex){
			info.setMessage(ex.getMessage());
			info.setPageType("Sign Up");
			return new ModelAndView("redirect:/site/signup");
		}
		if(emailSet.contains(form.getEmail()))
			p.setRole("supervisor");
		else
			p.setRole("operator");
		service.add(p);
		// Create authentication object
		Authentication authentication = convert(p);
		// Create new session
		HttpSession session = req.getSession(true);
		// Attach Spring SecurityContext to this new session
		SecurityUtil.createContext(session);
		// Attach Authentication object to the Security Context
		SecurityUtil.setAuthentication(authentication);

		return new ModelAndView("redirect:/ui/brand");
	}

	private static UserData convertToUserData(UserPojo p) {
		UserData d = new UserData();
		d.setEmail(p.getEmail());
		d.setRole(p.getRole());
		d.setId(p.getId());
		return d;
	}

	private static UserPojo convertToUserPojo(SignUpForm f) {
		UserPojo p = new UserPojo();
		p.setEmail(f.getEmail());
		p.setPassword(f.getPassword());
		return p;
	}

	private static Authentication convert(UserPojo p) {
		// Create principal
		UserPrincipal principal = new UserPrincipal();
		principal.setEmail(p.getEmail());
		principal.setRole(p.getRole());
		principal.setId(p.getId());

		// Create Authorities
		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(p.getRole()));
		// you can add more roles if required

		// Create Authentication
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
				authorities);
		return token;
	}
}
