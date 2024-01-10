package com.product.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.product.model.Product;
import com.product.service.ProductService;


@Controller
@RequestMapping("/api/products")
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	@GetMapping
	public ResponseEntity<?> getAllproducts() {
		List<Product> list = new ArrayList<Product>();
		try {
			list = productService.getAllProducts();
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	@GetMapping(value="/search",  produces="application/json")
	public ResponseEntity<Object> searchProducts(
	        @RequestParam(required = false) String productName,
	        @RequestParam(required = false) Integer minPrice,
	        @RequestParam(required = false) Integer maxPrice,
	        @RequestParam(required = false) Date minPostedDate,
	        @RequestParam(required = false) Date maxPostedDate) {
		  List<Product> list = null;
		  if(productName!=null) {
			  list =  productService.searchByproductName(productName);
		  }else if(minPrice>0 && maxPrice>0) {
			  list = productService.searchByMinandMaxprice(minPrice, maxPrice);
		  }else if(minPostedDate!=null && maxPostedDate!=null) {
			  list = productService.searchByPostedDate(minPostedDate, maxPostedDate);
		  }
		  else {
			  return new ResponseEntity<>("Please give Atlease one Parameter to Search the products", HttpStatus.BAD_REQUEST);
		  }
		  
				return new ResponseEntity<>(list, HttpStatus.OK);
	  
	}
	
	
	@PostMapping
	public ResponseEntity<?> createProduct(@RequestBody Product product) {
		if (product.getPrice() > 10000) {
			return new ResponseEntity<>("Price must not exceed $10,000.", HttpStatus.BAD_REQUEST);
		}
		return productService.saveproduct(product);
	}
	
	@PutMapping("/{productId}")
	public ResponseEntity<?>  updateproduct(@PathVariable Long productId, @RequestBody Product product){
		return productService.updateProduct(product, productId);
	}
	
	
	@DeleteMapping("/{productId}")
	public ResponseEntity<?>   deleteProfuct(@PathVariable Long productId){
		return productService.deleteProduct(productId);
	}
	
	@GetMapping("/approval-queue")
	public ResponseEntity<?>  getAllProductsInApprovalQueue(){
		return productService.getApprovalQueueProducts();
	}
	
	@PutMapping("/approval-queue/{approvalId}/approve")
	public ResponseEntity<?> approveproduct(@PathVariable Long approvalId){
		return productService.approveProducts(approvalId);
	}
	
	@PutMapping("/approval-queue/{approvalId}/reject")
	public ResponseEntity<?> rejectproduct(@PathVariable Long approvalId){
		return productService.rejectProducts(approvalId);
	}
	
	
	


}