package com.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.product.model.ApprovalDetails;


public interface ApprovalDetailsRepository extends JpaRepository<ApprovalDetails, Long> {

}
