package com.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "approval_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDetails {
	
	 @Id
	 @Column(name ="id")
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 
	 @OneToOne( mappedBy = "approvalDetails")
	 private Product  product;
	 
	 @Column(name = "approval_request_message")
	 private String approvalReqMsg;

}
