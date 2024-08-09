package com.increff.posapp.dto;

import com.increff.posapp.model.BrandData;
import com.increff.posapp.model.InventoryReportData;
import com.increff.posapp.model.SalesReportData;
import com.increff.posapp.model.SalesReportForm;
import com.increff.posapp.pojo.*;
import com.increff.posapp.service.*;
import com.increff.posapp.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class ReportsDto {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    public Page<BrandData> getBrandReport(String brand, String category, Integer page, Integer size) throws ApiException {
        brand = StringUtil.toLowerCase(brand);
        category = StringUtil.toLowerCase(category);
        if(page == null && size == null){
            List<BrandPojo> pojos = new ArrayList<>();
            if(StringUtil.isEmpty(brand) && StringUtil.isEmpty(category)){
                pojos = brandService.getAll();
            }
            else if(!StringUtil.isEmpty(brand) && !StringUtil.isEmpty(category)){
                pojos.add(brandService.getByBrandAndCategory(brand, category));
            }
            else if(!StringUtil.isEmpty(category)){
                pojos = brandService.getByCategory(category);
            }
            else {
                pojos = brandService.getByBrand(brand);
            }
            List<BrandData> list = Converter.convertToBrandDataList(pojos);
            return new PageImpl<>(list, PageRequest.of(0, list.size()), list.size());
        }
        if(page == null){
            throw new ApiException("Page can't be empty");
        }
        if(size == null){
            throw new ApiException("Size can't be empty");
        }
        if(StringUtil.isEmpty(brand) && StringUtil.isEmpty(category)){
            List<BrandPojo> brandList = brandService.getAll(page, size);
            return getPage(brandList, page, size, brandService.getTotalElements());
        }
        if(!StringUtil.isEmpty(brand) && !StringUtil.isEmpty(category)){
            BrandPojo brandPojo = brandService.getByBrandAndCategory(brand, category);
            List<BrandData> list = new ArrayList<>();
            list.add(Converter.convertToBrandData(brandPojo));
            return new PageImpl<>(list, PageRequest.of(page, size), 1 );
        }
        if(!StringUtil.isEmpty(brand)){
            List<BrandPojo> brandPojoList = brandService.getByBrand(brand, page, size);
            return getPage(brandPojoList, page, size, brandService.getByBrandTotalElements(brand));
        }
        List<BrandPojo> brandList = brandService.getByCategory(category, page, size);
        return getPage(brandList, page, size,
                    brandService.getCategoryTotalElements(category));
    }

    private Page<BrandData> getPage(List<BrandPojo> brandList, Integer page, Integer size, Long totalElements){
        List<BrandData> listBrandData = new ArrayList<>();
        for(BrandPojo p: brandList) {
            listBrandData.add(Converter.convertToBrandData(p));
        }
        return new PageImpl<>(listBrandData,
                PageRequest.of(page, size),
                totalElements);
    }

    public Page<InventoryReportData> getInventoryReport(String brand, String category, Integer page, Integer size) throws ApiException {
        brand = StringUtil.toLowerCase(brand);
        category = StringUtil.toLowerCase(category);
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();
        if(page == null && size == null){
            if(StringUtil.isEmpty(brand) && StringUtil.isEmpty(category)){
                for(InventoryPojo inventoryPojo: inventoryPojoList){
                    ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                    BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                    inventoryReportDataList.add(Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo));
                }
                validate(inventoryReportDataList);
            }
            else if(!StringUtil.isEmpty(brand) && !StringUtil.isEmpty(category)){
                for(InventoryPojo inventoryPojo: inventoryPojoList){
                    ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                    BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                    if(brandPojo.getBrand().equals(brand) && brandPojo.getCategory().equals(category)){
                        inventoryReportDataList.add(Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo));
                    }
                }
                validate(inventoryReportDataList);
            }
            else if(!StringUtil.isEmpty(brand)){
                for(InventoryPojo inventoryPojo: inventoryPojoList){
                    ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                    BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                    if(brandPojo.getBrand().equals(brand)){
                        inventoryReportDataList.add(Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo));
                    }
                }
                validate(inventoryReportDataList);
            }
            else {
                for(InventoryPojo inventoryPojo: inventoryPojoList){
                    ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                    BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                    if(brandPojo.getCategory().equals(category)){
                        inventoryReportDataList.add(Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo));
                    }
                }
                validate(inventoryReportDataList);
            }
            return new PageImpl<>(inventoryReportDataList,
                    PageRequest.of(0, inventoryReportDataList.size()),
                    inventoryReportDataList.size());
        }
        if(page == null){
            throw new ApiException("Page can't be empty");
        }
        if(size == null){
            throw new ApiException("Size can't be empty");
        }
        if(StringUtil.isEmpty(brand) && StringUtil.isEmpty(category)){
            for(InventoryPojo inventoryPojo: inventoryPojoList){
                ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                inventoryReportDataList.add(Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo));
            }
            validate(inventoryReportDataList);
            return getPage(inventoryReportDataList, page, size);
        }

        if(!StringUtil.isEmpty(brand) && !StringUtil.isEmpty(category)){
            for(InventoryPojo inventoryPojo: inventoryPojoList){
                ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                if(brandPojo.getBrand().equals(brand) && brandPojo.getCategory().equals(category)){
                    inventoryReportDataList.add(
                            Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo)
                    );
                }
            }
            validate(inventoryReportDataList);
            return getPage(inventoryReportDataList, page, size);
        }
        if(!StringUtil.isEmpty(brand)) {
            for (InventoryPojo inventoryPojo : inventoryPojoList) {
                ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
                BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                if (brandPojo.getBrand().equals(brand)) {
                    inventoryReportDataList.add(
                            Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo)
                    );
                }
            }
            validate(inventoryReportDataList);
            return getPage(inventoryReportDataList, page, size);
        }
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            ProductPojo productPojo = productService.getById(inventoryPojo.getProductId());
            BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
            if(brandPojo.getCategory().equals(category)){
                inventoryReportDataList.add(
                        Converter.convertToInventoryReportData(inventoryPojo, productPojo, brandPojo)
                );
            }
        }
        validate(inventoryReportDataList);
        return getPage(inventoryReportDataList, page, size);

    }
    private Page<InventoryReportData> getPage(List<InventoryReportData> inventoryReportDataList, Integer page, Integer size){
        List<InventoryReportData> inventoryReportDataList1 = new ArrayList<>();
        Long totalElements = (long) inventoryReportDataList.size();
        Integer start = page*size;
        Integer end = start + size - 1;
        for(int i=start; i <= end && i < inventoryReportDataList.size(); i++){
            inventoryReportDataList1.add(inventoryReportDataList.get(i));
        }
        return new PageImpl<>(inventoryReportDataList1, PageRequest.of(page, size), inventoryReportDataList.size());
    }

    private void validate(List<InventoryReportData> list) throws ApiException {
        if(list.isEmpty()){
            throw new ApiException("No items available with the given details");
        }
    }

    public SalesReportData getSalesReport(SalesReportForm salesReportForm, Integer page, Integer size) throws ApiException {
        validate(salesReportForm);
        ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        ZonedDateTime zonedDateTimeStart = ZonedDateTime.of(salesReportForm.getStartDate(), zoneId);
        ZonedDateTime zonedDateTimeEnd = ZonedDateTime.of(salesReportForm.getEndDate(), zoneId);

        validate(salesReportForm);

        SortedMap<String, Double> sm1 = new TreeMap<>();
        SortedMap<String, Integer> sm2 = new TreeMap<>();
        SalesReportData salesReportData = new SalesReportData();
        Double totalRevenue = 0.0;

        List<OrderPojo> orderPojoList = orderService.getByInterval(zonedDateTimeStart, zonedDateTimeEnd);

        for(OrderPojo orderPojo: orderPojoList){
            List<OrderItemPojo> orderItemPojoList = orderItemService.getByOrderId(orderPojo.getId());
            for(OrderItemPojo orderItemPojo: orderItemPojoList){
                ProductPojo productPojo = productService.getById(orderItemPojo.getProductId());
                BrandPojo brandPojo = brandService.getById(productPojo.getBrandCategory());
                if(!StringUtil.isEmpty(salesReportForm.getBrand() )&& !StringUtil.isEmpty(salesReportForm.getCategory())){
                    if(brandPojo.getBrand().equals(salesReportForm.getBrand()) && brandPojo.getCategory().equals(salesReportForm.getCategory())){
                        setSortedMap1(sm1, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                        setSortedMap2(sm2, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                        totalRevenue += orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
                    }
                }
                else if(!StringUtil.isEmpty(salesReportForm.getBrand())){
                    if(brandPojo.getBrand().equals(salesReportForm.getBrand())){
                        setSortedMap1(sm1, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                        setSortedMap2(sm2, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                        totalRevenue += orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
                    }
                    else {
                        continue;
                    }
                }
                else if(!StringUtil.isEmpty(salesReportForm.getCategory())){
                    if(brandPojo.getCategory().equals(salesReportForm.getCategory())){
                        setSortedMap1(sm1, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                        setSortedMap2(sm2, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                        totalRevenue += orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
                    }
                }
                else{
                    setSortedMap1(sm1, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                    setSortedMap2(sm2, orderItemPojo, brandPojo.getBrand(), brandPojo.getCategory());
                    totalRevenue += orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
                }
            }
        }
        salesReportData.setStartDate(DateTimeUtil.getDateTimeString(salesReportForm.getStartDate(),"dd/MM/yyyy"));
        salesReportData.setEndDate(DateTimeUtil.getDateTimeString(salesReportForm.getEndDate(), "dd/MM/yyyy"));
        salesReportData.setTotalRevenue(DoubleUtil.roundToString(totalRevenue));
        for(Map.Entry<String, Double> entry: sm1.entrySet()){
            Integer quantity = sm2.get(entry.getKey());
            Converter.convertToSalesReportData(salesReportData, entry.getKey(), entry.getValue(), quantity);
        }

        if(page == null && size == null){
            return salesReportData;
        }
        SalesReportData salesReportDataPage = new SalesReportData();
        salesReportDataPage.setStartDate(salesReportData.getStartDate());
        salesReportDataPage.setEndDate(salesReportData.getEndDate());
        salesReportDataPage.setTotalRevenue(salesReportData.getTotalRevenue());

        for(int i=page*size; i < salesReportData.getBrands().size() && i < page*size + size; i++){
            salesReportDataPage.getBrands().add(salesReportData.getBrands().get(i));
            salesReportDataPage.getCategories().add(salesReportData.getCategories().get(i));
            salesReportDataPage.getQuantities().add(salesReportData.getQuantities().get(i));
            salesReportDataPage.getTotalAmounts().add(salesReportData.getTotalAmounts().get(i));
        }
        salesReportDataPage.setTotalElements((long) salesReportData.getBrands().size());
        return salesReportDataPage;
    }

    private void setSortedMap1(SortedMap<String, Double> sm, OrderItemPojo orderItemPojo, String brand, String category){
        String brandCategory = brand + "--" + category;
        if(sm.containsKey(brandCategory)){
            sm.put(brandCategory, sm.get(brandCategory) + orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());
        }
        else{
            sm.put(brandCategory, orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());
        }
    }
    private void setSortedMap2(SortedMap<String, Integer> sm, OrderItemPojo orderItemPojo, String brand, String category){
        String brandCategory = brand + "--" + category;
        if(sm.containsKey(brandCategory)){
            sm.put(brandCategory, sm.get(brandCategory) + orderItemPojo.getQuantity());
        }
        else{
            sm.put(brandCategory, orderItemPojo.getQuantity());
        }
    }

    private void validate(SalesReportForm salesReportForm) throws ApiException {
        if(!StringUtil.isEmpty(salesReportForm.getBrand())){
            brandService.getByBrand(salesReportForm.getBrand());
        }
        if(!StringUtil.isEmpty(salesReportForm.getCategory())){
            brandService.getByCategory(salesReportForm.getCategory());
        }
        if(salesReportForm.getStartDate().toLocalDate().isBefore(LocalDate.now().minusYears(2L))){
            throw new ApiException("Enter a date within 2 years");
        }
        if(salesReportForm.getEndDate().toLocalDate().isAfter(LocalDate.now())){
            throw new ApiException("End date should be today's date or a date before today");
        }
        if(ChronoUnit.DAYS.between(salesReportForm.getStartDate().toLocalDate(), salesReportForm.getEndDate().toLocalDate()) > 366){
            throw new ApiException("Difference between the two entered dates must be within one year");
        }
    }
}
