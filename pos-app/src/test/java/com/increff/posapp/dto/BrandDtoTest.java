package com.increff.posapp.dto;

import com.increff.posapp.model.BrandData;
import com.increff.posapp.model.BrandForm;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
public class BrandDtoTest extends AbstractUnitTest {

    @Autowired
    private BrandDto brandDto;

    private static Logger logger = Logger.getLogger(BrandDtoTest.class);

    private BrandData addBrand() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand("brand1");
        form.setCategory("category1");
        return brandDto.add(form);
    }
    @Test
    public void testAdd() throws ApiException, IllegalAccessException {
        BrandData data = addBrand();
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertNotNull(data.getId());
    }

    @Test(expected = ApiException.class)
    public void testAddBrandNull() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand(null);
        brandDto.add(form);
    }

    @Test(expected = ApiException.class)
    public void testAddBrandEmpty() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand("");
        brandDto.add(form);
    }
    @Test(expected = ApiException.class)
    public void testAddBrandNotAlNum() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand("/*76%$fftad...");
        brandDto.add(form);
    }
    @Test(expected = ApiException.class)
    public void testAddCategoryNull() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand("brand1");
        form.setCategory(null);
        brandDto.add(form);
    }
    @Test(expected = ApiException.class)
    public void testAddCategoryEmpty() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand("brand1");
        form.setCategory("");
        brandDto.add(form);
    }

    @Test(expected = ApiException.class)
    public void testAddCategoryNotAlNum() throws ApiException, IllegalAccessException {
        BrandForm form = new BrandForm();
        form.setBrand("brand1");
        form.setCategory("/&$#khihaiuf");
        brandDto.add(form);
    }
    @Test
    public void testGetById() throws ApiException, IllegalAccessException {
        BrandData data = addBrand();
        BrandData brandData = brandDto.get(data.getId());
        assertEquals(data.getId(), brandData.getId());
    }

    @Test(expected = ApiException.class)
    public void testGetDataIdNull() throws ApiException, IllegalAccessException {
        brandDto.get(null);
    }

    @Test(expected = ApiException.class)
    public void testGetDataPageNull() throws ApiException, IllegalAccessException {
        BrandData data = addBrand();
        brandDto.get(null, 4);
    }

    @Test(expected = ApiException.class)
    public void testGetDataSizeNull() throws ApiException, IllegalAccessException {
        BrandData data = addBrand();
        brandDto.get(0, null);
    }

    @Test
    public void testGetInPage() throws ApiException, IllegalAccessException {
        BrandData data = addBrand();
        List<BrandData> brandDataList =  brandDto.get(0, 5).getContent();
        assertEquals(data.getId(), brandDataList.get(0).getId());
        assertEquals(data.getBrand(), brandDataList.get(0).getBrand());
        assertEquals(data.getCategory(), brandDataList.get(0).getCategory());
        assertTrue(brandDataList.size() >0 && brandDataList.size() <= 5);
    }
    @Test
    public void testUpdateById() throws ApiException, IllegalAccessException {
        BrandData data = addBrand();
        BrandForm form = new BrandForm();
        form.setBrand("brand2");
        form.setCategory("cate2");
        BrandData brandData = brandDto.update(data.getId(), form);
        assertEquals("brand2", brandData.getBrand());
        assertEquals("cate2", brandData.getCategory());
    }
}
