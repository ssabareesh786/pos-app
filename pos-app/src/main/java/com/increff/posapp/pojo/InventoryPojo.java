package com.increff.posapp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@ToString
public class InventoryPojo {
	@Id
	@Column(name = "product_id")
	private Integer productId;
	@Column
	private Integer quantity;
}
