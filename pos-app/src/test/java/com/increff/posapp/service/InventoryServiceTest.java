package com.increff.posapp.service;

import com.increff.posapp.pojo.BrandPojo;
import com.increff.posapp.pojo.InventoryPojo;
import com.increff.posapp.pojo.ProductPojo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InventoryServiceTest extends AbstractUnitTest {

    @Autowired
    private BrandService brandService;
	@Autowired
	private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    private BrandPojo addBrand() throws ApiException {
        BrandPojo p = new BrandPojo();
        p.setBrand("brand1");
        p.setCategory("category1");
        return brandService.add(p);
    }
    private InventoryPojo addProductInInventory() throws ApiException {
        ProductPojo pojo = new ProductPojo();
        pojo.setBarcode("asdfgfhu");
        pojo.setName("product1");
        pojo.setBrandCategory(addBrand().getId());
        pojo.setMrp(339.765);
        ProductPojo pojo1 = productService.add(pojo);
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setProductId(pojo1.getId());
        inventoryPojo.setQuantity(0);
        return inventoryService.add(inventoryPojo);

    }

    @Test(expected = ApiException.class)
    public void testValidateProductIdNull() throws ApiException {
        InventoryPojo p = new InventoryPojo();
        p.setProductId(null);
        p.setQuantity(78);
        inventoryService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateQuantityNull() throws ApiException {
        InventoryPojo p = new InventoryPojo();
        p.setProductId(1);
        p.setQuantity(null);
        inventoryService.add(p);
    }
    @Test(expected = ApiException.class)
    public void testValidateQuantityNegative() throws ApiException {
        InventoryPojo p = new InventoryPojo();
        p.setProductId(1);
        p.setQuantity(-8);
        inventoryService.add(p);
    }

    @Test
    public void testAdd() throws ApiException {
        InventoryPojo pojo1 = addProductInInventory();
        InventoryPojo pojo2 = new InventoryPojo();
        pojo2.setProductId(pojo1.getProductId());
        pojo2.setQuantity(10);
        InventoryPojo pojo = inventoryService.add(pojo2);
        assertEquals(pojo1.getProductId(), pojo.getProductId());
        assertEquals("10", pojo.getQuantity().toString());
    }

    @Test
    public void testUpdateByProductId() throws ApiException {
        InventoryPojo pojo1 = addProductInInventory();
        InventoryPojo pojo2 = new InventoryPojo();
        pojo2.setProductId(pojo1.getProductId());
        pojo2.setQuantity(10);
        InventoryPojo pojo = inventoryService.update(pojo2);
        assertEquals(pojo1.getProductId(), pojo.getProductId());
        assertEquals("10", pojo.getQuantity().toString());
    }

    @Test(expected = ApiException.class)
    public void testUpdateByProductIdInvalid() throws ApiException {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(100002);
        pojo.setQuantity(10);
        inventoryService.update(pojo);
    }

    @Test
    public void testGetAll() throws ApiException {
        addProductInInventory();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        assertTrue(inventoryPojoList.size() > 0);
    }
    @Test
    public void testGetAllByPage() throws ApiException {
        addProductInInventory();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll(0,5);
        assertTrue(inventoryPojoList.size() > 0 && inventoryPojoList.size() <= 5);
    }

    @Test
    public void testGetTotalElements() throws ApiException {
        addProductInInventory();
        Long totalElements = inventoryService.getTotalElements();
        assertTrue(totalElements > 0);
    }
}
