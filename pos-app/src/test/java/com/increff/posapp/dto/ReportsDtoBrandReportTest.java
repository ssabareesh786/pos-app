package com.increff.posapp.dto;

import com.increff.posapp.dao.BrandDao;
import com.increff.posapp.model.BrandData;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.*;

public class ReportsDtoBrandReportTest extends AbstractUnitTest {

    @Autowired
    private ReportsDto reportsDto;
    @Autowired
    private BrandDao brandDao;

    private BrandPojo addBrand(Integer i){
        BrandPojo p1 = new BrandPojo();
        p1.setBrand("brand"+i.toString());
        p1.setCategory("category"+i.toString());
        return (BrandPojo) brandDao.insert(p1);
    }

    private BrandPojo addBrand(Integer b, Integer c){
        BrandPojo p1 = new BrandPojo();
        p1.setBrand("brand"+b.toString());
        p1.setCategory("category"+c.toString());
        return (BrandPojo) brandDao.insert(p1);
    }
    @Test
    public void testGetBrandReportPageAndSizeNull() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        List<BrandData> list = reportsDto.getBrandReport("", "", null, null).getContent();
        assertEquals(3, list.size());
    }

    @Test(expected = ApiException.class)
    public void testGetBrandReportPageNull() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        List<BrandData> list = reportsDto.getBrandReport("", "", null, 9).getContent();
        assertEquals(3, list.size());
    }

    @Test(expected = ApiException.class)
    public void testGetBrandReportSizeNull() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        reportsDto.getBrandReport("", "", 0, null);
    }

    @Test
    public void testGetBrandReportBrandNotEmpty() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        List<BrandData> list = reportsDto.getBrandReport("brand1", "", null, null).getContent();
        assertEquals(1, list.size());
    }

    @Test
    public void testGetBrandReportCategoryNotEmpty() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        List<BrandData> list = reportsDto.getBrandReport("", "category1", null, null).getContent();
        assertEquals(1, list.size());
    }

    @Test
    public void testGetBrandReportBrandAndCategory() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        List<BrandData> list = reportsDto.getBrandReport("brand1", "category1", null, null).getContent();
        assertEquals(1, list.size());
    }

    @Test
    public void testGetBrandReportByPage() throws ApiException {
        BrandPojo p1 = addBrand(1);
        BrandPojo p2 = addBrand(2);
        BrandPojo p3 = addBrand(3);
        Page<BrandData> page = reportsDto.getBrandReport("", "", 0, 2);
        List<BrandData> list = page.getContent();
        assertEquals(2, list.size());
        assertEquals("brand1", list.get(0).getBrand());
        assertEquals("category1", list.get(0).getCategory());
        assertEquals("brand2", list.get(1).getBrand());
        assertEquals("category2", list.get(1).getCategory());
    }

    @Test
    public void testGetBrandReportByPageBrand() throws ApiException {
        BrandPojo p1 = addBrand(1, 1);
        BrandPojo p2 = addBrand(1, 2);
        BrandPojo p3 = addBrand(1, 3);
        BrandPojo p4 = addBrand(2, 1);
        Page<BrandData> page = reportsDto.getBrandReport("brand1", "", 0, 2);
        List<BrandData> list = page.getContent();
        assertEquals(3, page.getTotalElements());
        assertEquals(2, list.size());
        assertEquals("brand1", list.get(0).getBrand());
        assertEquals("brand1", list.get(1).getBrand());
        assertEquals("category1", list.get(0).getCategory());
        assertEquals("category2", list.get(1).getCategory());
    }

    @Test
    public void testGetBrandReportByPageCategory() throws ApiException {
        BrandPojo p1 = addBrand(1, 1);
        BrandPojo p2 = addBrand(2, 1);
        BrandPojo p3 = addBrand(3, 1);
        BrandPojo p4 = addBrand(4, 1);
        Page<BrandData> page = reportsDto.getBrandReport("", "category1", 0, 2);
        List<BrandData> list = page.getContent();
        assertEquals(4, page.getTotalElements());
        assertEquals(2, list.size());
        assertEquals("brand1", list.get(0).getBrand());
        assertEquals("brand2", list.get(1).getBrand());
        assertEquals("category1", list.get(0).getCategory());
        assertEquals("category1", list.get(1).getCategory());
    }

    @Test
    public void testGetBrandReportByPageBoth() throws ApiException {
        BrandPojo p1 = addBrand(1, 1);
        BrandPojo p2 = addBrand(2, 1);
        BrandPojo p3 = addBrand(3, 1);
        BrandPojo p4 = addBrand(4, 1);
        Page<BrandData> page = reportsDto.getBrandReport("brand1", "category1", 0, 2);
        List<BrandData> list = page.getContent();
        assertEquals(1, page.getTotalElements());
        assertEquals(1, list.size());
        assertEquals("brand1", list.get(0).getBrand());
        assertEquals("category1", list.get(0).getCategory());
    }
}
