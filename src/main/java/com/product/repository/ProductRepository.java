package com.product.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.product.model.Product;



@Repository
public interface ProductRepository extends  JpaRepository<Product, Long> {
	
    List<Product> findByActiveTrueOrderByPostedDateDesc();
    
    List<Product> findByProductName(String productName);
    
    List<Product> findByPriceBetween(int minPrice, int maxPrice);
    
    List<Product> findByPostedDateBetween(Date minPrice, Date maxPrice);
    
    List<Product> findAllByOrderByPostedDateAsc();

}
