package com.increff.posapp.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "pos_day_sales")
@Getter
@Setter
@ToString
public class PosDaySalesPojo {

    @Id
    @Column(name = "date")
    private ZonedDateTime date;
    @Column(name = "invoiced_orders_count", nullable = false)
    private Integer invoicedOrdersCount;
    @Column(name = "invoiced_items_count", nullable = false)
    private Integer invoicedItemsCount;
    @Column(name = "total_revenue", nullable = false)
    private Double totalRevenue;
}
