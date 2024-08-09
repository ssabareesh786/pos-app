package com.increff.posapp.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
		name = "orderitems",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "order_id", "product_id" }) },
		indexes = {@Index(name="uniqueIndex", columnList = "order_id, product_id", unique = true)}
)
@Getter
@Setter
@ToString
public class OrderItemPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "order_id", nullable = false)
	private Integer orderId;
	@Column(name = "product_id", nullable = false)
	private Integer productId;
	@Column(nullable = false)
	private Integer quantity;
	@Column(name = "selling_price", nullable = false)
	private Double sellingPrice;
}
