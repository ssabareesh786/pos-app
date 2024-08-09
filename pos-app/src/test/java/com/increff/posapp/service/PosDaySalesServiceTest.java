package com.increff.posapp.service;


import com.increff.posapp.pojo.PosDaySalesPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class PosDaySalesServiceTest extends AbstractUnitTest {

    @Autowired
    private PosDaySalesService posDaySalesService;

    @Test
    public void testAdd() throws ApiException {
        PosDaySalesPojo p = new PosDaySalesPojo();
        p.setDate(ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("Asia/Kolkata")));
        p.setInvoicedOrdersCount(1);
        p.setInvoicedItemsCount(1);
        p.setTotalRevenue(23.45);
        posDaySalesService.add(p);
    }

    @Test
    public void testGetLastDateTime(){
        posDaySalesService.getLastDateTime();
    }

    @Test
    public void testGetAll(){
        posDaySalesService.getAll();
    }

    @Test
    public void testGetAllByPage(){
        List<PosDaySalesPojo> pojos = posDaySalesService.getAll(0, 5);
        assertTrue(pojos.size() <= 5);
    }

    @Test
    public void testGetByInterval(){
        ZonedDateTime startDate = ZonedDateTime.of(LocalDateTime.now().minusDays(12L),
                ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endDate = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Asia/Kolkata"));
        List<PosDaySalesPojo> pojos = posDaySalesService.getByInterval(startDate, endDate);
    }
}
