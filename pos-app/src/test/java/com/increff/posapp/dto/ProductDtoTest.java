package com.increff.posapp.dto;

import com.increff.posapp.dao.BrandDao;
import com.increff.posapp.dao.InventoryDao;
import com.increff.posapp.model.ProductData;
import com.increff.posapp.model.ProductForm;
import com.increff.posapp.model.ProductInventoryData;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.pojo.InventoryPojo;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.*;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto productDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private InventoryDao inventoryDao;
    private static Logger logger = Logger.getLogger(ProductDtoTest.class);

    private BrandPojo addBrand(){
        BrandPojo pojo = new BrandPojo();
        pojo.setBrand("brand1");
        pojo.setCategory("category1");
        return (BrandPojo) brandDao.insert(pojo);
    }
    private ProductData addProduct() throws ApiException, IllegalAccessException {
        ProductForm form = new ProductForm();
        form.setBarcode("asd3455t5");
        form.setBrand("brand1");
        form.setCategory("category1");
        form.setName("product1");
        form.setMrp(123.45);
        return productDto.add(form);
    }

    private void addInventory(Integer productId){
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(productId);
        pojo.setQuantity(12);
        inventoryDao.insert(pojo);
    }
    @Test
    public void testAdd() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data = addProduct();
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product1", data.getName());
        assertEquals("123.45", data.getMrp().toString());
        assertEquals("asd3455t5", data.getBarcode());
        assertEquals(pojo.getId(), data.getBrandCategory());
    }

    @Test
    public void testGetById() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        ProductData data = productDto.get(data1.getId());
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product1", data.getName());
        assertEquals("123.45", data.getMrp().toString());
        assertEquals("asd3455t5", data.getBarcode());
        assertEquals(pojo.getId(), data.getBrandCategory());
    }

    @Test
    public void testGetByIdInventoryNotEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        addInventory(data1.getId());
        ProductData data = productDto.get(data1.getId());
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product1", data.getName());
        assertEquals("123.45", data.getMrp().toString());
        assertEquals("asd3455t5", data.getBarcode());
        assertEquals(pojo.getId(), data.getBrandCategory());
    }

    @Test(expected = ApiException.class)
    public void testGetByIdInvalid() throws ApiException, IllegalAccessException {
        addBrand();
        ProductData data1 = addProduct();
        productDto.get(data1.getId() + 200000);
    }

    @Test(expected = ApiException.class)
    public void testGetByBarcodeInventoryEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        productDto.get(data1.getBarcode(), true, null, null);
    }

    @Test
    public void testGetByBarcodeInventoryStatusTrue() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        addInventory(data1.getId());
        ProductInventoryData data = (ProductInventoryData) productDto.get(data1.getBarcode(), true, null, null);
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product1", data.getName());
        assertEquals("123.45", data.getMrp().toString());
        assertEquals("asd3455t5", data.getBarcode());
        assertEquals("12", data.getQuantity().toString());
    }

    @Test
    public void testGetByBarcodeInventoryStatusFalse() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        addInventory(data1.getId());
        ProductData data = (ProductData) productDto.get(data1.getBarcode(), false, null, null);
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product1", data.getName());
        assertEquals("123.45", data.getMrp().toString());
        assertEquals("asd3455t5", data.getBarcode());
    }

    @Test(expected = ApiException.class)
    public void testGetByBarcodeInvalid() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        ProductData data = (ProductData) productDto.get("./Y7854$56^",true, null, null);
    }

    @Test
    public void testGetAllInventoryEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        Page<ProductData> page = (Page<ProductData>) productDto.get(null,false, 0,5);
        List<ProductData> dataList = page.getContent();
        assertTrue(dataList.size() > 0 && dataList.size() <= 5);
    }

    @Test
    public void testGetAllInventoryNotEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        addInventory(data1.getId());
        Page<ProductData> page = (Page<ProductData>) productDto.get(null, false,0,5);
        List<ProductData> dataList = page.getContent();
        assertTrue(dataList.size() > 0 && dataList.size() <= 5);
    }

    @Test
    public void testUpdateByIdInventoryEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        ProductForm form = new ProductForm();
        form.setBarcode("asd34455");
        form.setBrand("brand1");
        form.setCategory("category1");
        form.setName("product2");
        form.setMrp(13.45);
        ProductData data = productDto.update(data1.getId(), form);
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product2", data.getName());
        assertEquals("13.45", data.getMrp().toString());
        assertEquals("asd34455", data.getBarcode());
        assertEquals(data1.getBrandCategory(), data.getBrandCategory());
    }

    @Test
    public void testUpdateByIdInventoryNotEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        addInventory(data1.getId());
        ProductForm form = new ProductForm();
        form.setBarcode("asd34455");
        form.setBrand("brand1");
        form.setCategory("category1");
        form.setName("product2");
        form.setMrp(13.45);
        ProductData data = productDto.update(data1.getId(), form);
        assertEquals("brand1", data.getBrand());
        assertEquals("category1", data.getCategory());
        assertEquals("product2", data.getName());
        assertEquals("13.45", data.getMrp().toString());
        assertEquals("asd34455", data.getBarcode());
        assertEquals(data1.getBrandCategory(), data.getBrandCategory());
    }

    @Test
    public void testGetDataNull() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        Page<ProductData> page = (Page<ProductData>) productDto.get(null, false,0, 5);
        List<ProductData> dataList = page.getContent();
        assertTrue(dataList.size()>0 && dataList.size() <=5);
    }

    @Test(expected = ApiException.class)
    public void testGetDataBarcodeInventoryEmpty() throws ApiException, IllegalAccessException {
        BrandPojo pojo = addBrand();
        ProductData data1 = addProduct();
        ProductInventoryData data = (ProductInventoryData) productDto.get(data1.getBarcode(), true, null, null);
    }

    @Test
    public void testGetDataBarcodeInventoryNotEmpty() throws ApiException, IllegalAccessException {
        BrandPojo brandPojo = addBrand();
        ProductData productData = addProduct();
        addInventory(productData.getId());
        ProductInventoryData data = (ProductInventoryData) productDto.get(productData.getBarcode(), true, null, null);
        assertEquals(productData.getBarcode(), data.getBarcode());
        assertEquals(productData.getName(), data.getName());
        assertEquals(brandPojo.getBrand(), data.getBrand());
        assertEquals(brandPojo.getCategory(), data.getCategory());
        assertEquals(productData.getMrp(), data.getMrp());
        assertEquals("12", data.getQuantity().toString());
    }

    @Test(expected = ApiException.class)
    public void testGetDataPageNull() throws ApiException, IllegalAccessException {
        addBrand();
        addProduct();
        productDto.get(null, false, null, 9);
    }

    @Test(expected = ApiException.class)
    public void testGetDataSizeNull() throws ApiException, IllegalAccessException {
        addBrand();
        addProduct();
        productDto.get(null, false,0, null);
    }

    @Test(expected = ApiException.class)
    public void testGetDataInvalid() throws ApiException {
        productDto.get(null, null,null, null);
    }
}
