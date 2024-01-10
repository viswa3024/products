package com.product.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	
	@Id
	@Column(name ="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long  id;
	
	@Column (name = "product_name")
	private String productName;
	
	@Column(name = "price")
	private int price;
	
	@Column(name = "posted_date")
	private LocalDateTime  postedDate;
	
	@JsonProperty
	@Column(name = "active")
	private boolean active;
	
	
	@Column(name = "approval_queue", nullable = true)
	private boolean approvalQueue;
	
	@JsonProperty
	@Column(name = "is_approved",  nullable = true)
	private boolean isApproved;
	 
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "approval_id", unique = true)
	 private ApprovalDetails approvalDetails;

}
