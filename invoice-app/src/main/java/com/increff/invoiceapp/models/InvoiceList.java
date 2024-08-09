package com.increff.invoiceapp.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class InvoiceList {
    @XmlElement(name = "date")
    private String date;
    @XmlElement(name = "orderId")
    private Integer orderId;
    @XmlElement(name = "item")
    private List<InvoiceItem> items;
    @XmlElement(name = "total")
    private String total;
    public InvoiceList(){
        this.items = new ArrayList<>();
    }
}
