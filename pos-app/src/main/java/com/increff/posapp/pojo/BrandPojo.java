package com.increff.posapp.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "brands",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = { "brand", "category" }) },
		indexes = {
			@Index(name = "uniqueIndex", columnList = "brand, category", unique = true)
		}
)
public class BrandPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String brand;
	@Column(nullable = false)
	private String category;
}
