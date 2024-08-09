package com.increff.invoiceapp.models;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class InvoiceItem {
    @XmlElement(name = "sno")
    private Integer sno;
    @XmlElement(name = "productName")
    private String productName;
    @XmlElement(name = "quantity")
    private int quantity;
    @XmlElement(name = "mrp")
    private String mrp;
    @XmlElement(name = "sellingPrice")
    private String sellingPrice;
    @XmlElement(name = "subTotal")
    private String subTotal;
}
