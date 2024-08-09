package com.increff.posapp.dao;

import com.increff.posapp.pojo.UserPojo;
import com.increff.posapp.service.AbstractUnitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
public class UserDaoTest extends AbstractUnitTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void testSelectId(){
        UserPojo pojo = new UserPojo();
        pojo.setEmail("user1@gmail.com");
        pojo.setPassword("1234");
        pojo.setRole("operator");
        UserPojo pojo1 = (UserPojo) userDao.insert(pojo);

        UserPojo userPojo = userDao.select(pojo1.getId());
        assertEquals("user1@gmail.com", userPojo.getEmail());
        assertEquals("1234", userPojo.getPassword());
        assertEquals("operator", userPojo.getRole());
    }

    @Test
    public void testSelectEmail(){
        UserPojo pojo = new UserPojo();
        pojo.setEmail("user1@gmail.com");
        pojo.setPassword("1234");
        pojo.setRole("operator");
        UserPojo pojo1 = (UserPojo) userDao.insert(pojo);

        UserPojo userPojo = userDao.select(pojo1.getEmail());
        assertEquals("user1@gmail.com", userPojo.getEmail());
        assertEquals("1234", userPojo.getPassword());
        assertEquals("operator", userPojo.getRole());
    }

}
