package com.increff.posapp.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"brand_category", "product_name"})},
		indexes = {
				@Index(columnList = "brand_category, product_name", unique = true),
				@Index(columnList = "barcode", unique = true)
}
)
@Getter
@Setter
@ToString
public class ProductPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique = true, nullable = false)
	private String barcode;
	@Column(name = "brand_category", nullable = false)
	private Integer brandCategory;
	@Column(name = "product_name", nullable = false)
	private String name;
	@Column(nullable = false)
	private Double mrp;
}
