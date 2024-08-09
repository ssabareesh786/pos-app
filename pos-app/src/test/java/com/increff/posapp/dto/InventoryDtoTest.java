package com.increff.posapp.dto;

import com.increff.posapp.dao.BrandDao;
import com.increff.posapp.dao.ProductDao;
import com.increff.posapp.model.InventoryData;
import com.increff.posapp.model.InventoryForm;
import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.pojo.ProductPojo;
import com.increff.posapp.service.AbstractUnitTest;
import com.increff.posapp.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto inventoryDto;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ProductDao productDao;
    private BrandPojo addBrand(){
        BrandPojo pojo = new BrandPojo();
        pojo.setBrand("brand1");
        pojo.setCategory("category1");
        return (BrandPojo) brandDao.insert(pojo);
    }
    private ProductPojo addProduct() throws ApiException {
        BrandPojo brandPojo = addBrand();
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("barcode123");
        pojo.setBrandCategory(brandPojo.getId());
        pojo.setName("product1");
        pojo.setMrp(123.45);
        return (ProductPojo) productDao.insert(pojo);
    }

    private InventoryData addInventory() throws ApiException, IllegalAccessException {
        ProductPojo pojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(pojo.getBarcode());
        form.setQuantity(12);
        return inventoryDto.add(form);
    }
    @Test
    public void testAdd() throws ApiException, IllegalAccessException {
        ProductPojo productPojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(productPojo.getBarcode());
        form.setQuantity(12);
        InventoryData data = inventoryDto.add(form);
        assertEquals(productPojo.getBarcode(), data.getBarcode());
        assertEquals(productPojo.getId(), data.getProductId());
        assertEquals(12, data.getQuantity().intValue());
    }

    @Test
    public void testGetDataProductId() throws ApiException, IllegalAccessException {
        ProductPojo productPojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(productPojo.getBarcode());
        form.setQuantity(12);
        InventoryData data = inventoryDto.add(form);
        InventoryData inventoryData = inventoryDto.get(data.getProductId());
        assertEquals(data.getProductId(), inventoryData.getProductId());
        assertEquals("12", inventoryData.getQuantity().toString());
    }

    @Test(expected = ApiException.class)
    public void testGetDataProductIdNull() throws ApiException, IllegalAccessException {
        inventoryDto.get(null);
    }

    @Test
    public void testGetDataInPage() throws ApiException, IllegalAccessException {
        ProductPojo productPojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(productPojo.getBarcode());
        form.setQuantity(12);
        InventoryData data = inventoryDto.add(form);
        List<InventoryData> dataList = inventoryDto.get(0, 9).getContent();
        assertEquals(1, dataList.size());
    }

    @Test(expected = ApiException.class)
    public void testGetDataPageNull() throws ApiException, IllegalAccessException {
        ProductPojo productPojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(productPojo.getBarcode());
        form.setQuantity(12);
        InventoryData data = inventoryDto.add(form);
        List<InventoryData> dataList = inventoryDto.get(null, 9).getContent();
    }

    @Test(expected = ApiException.class)
    public void testGetDataSizeNull() throws ApiException, IllegalAccessException {
        ProductPojo productPojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(productPojo.getBarcode());
        form.setQuantity(12);
        InventoryData data = inventoryDto.add(form);
        List<InventoryData> dataList = inventoryDto.get(0, null).getContent();
    }

    @Test(expected = ApiException.class)
    public void testGetDataInvalid() throws ApiException {
        inventoryDto.get(null, null);
    }

    @Test
    public void testUpdate() throws ApiException, IllegalAccessException {
        ProductPojo pojo = addProduct();
        InventoryForm form = new InventoryForm();
        form.setBarcode(pojo.getBarcode());
        form.setQuantity(12);
        InventoryData data = inventoryDto.add(form);
        form.setBarcode(pojo.getBarcode());
        form.setQuantity(21);
        InventoryData dataReturned = inventoryDto.update(pojo.getId(), form);
        assertEquals(pojo.getId(), dataReturned.getProductId());
        assertEquals(pojo.getBarcode(), dataReturned.getBarcode());
        assertEquals(21, dataReturned.getQuantity().intValue());
    }
}
