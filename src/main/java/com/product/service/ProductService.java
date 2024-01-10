package com.product.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.product.model.ApprovalDetails;
import com.product.model.Product;
import com.product.repository.ApprovalDetailsRepository;
import com.product.repository.ProductRepository;



@Service
public class ProductService {
	
	@Autowired
	ProductRepository  productRepository;
	
	@Autowired
	ApprovalDetailsRepository approvalDetailsRepository;
	
	
	
	public List<Product> getAllProducts(){
		return productRepository.findByActiveTrueOrderByPostedDateDesc();
	}
	
	public List<Product>  searchByproductName(String productName){
		return productRepository.findByProductName(productName);
	}
	
	public List<Product>  searchByMinandMaxprice(int minPrice, int maxPrice){
		return productRepository.findByPriceBetween(minPrice, maxPrice);
	}
	
	public List<Product>  searchByPostedDate(Date minPostedDate , Date maxPostedDate){
		return productRepository.findByPostedDateBetween(minPostedDate, minPostedDate);
	}
	

	public ResponseEntity<?> saveproduct(Product product) {
		
		if(product.getPrice() > 5000) {
			product.setApprovalQueue(true);
		}else {
			product.setApprovalQueue(false);
		}

		try {
			if (!product.isApprovalQueue()) {
				
				productRepository.save(product);
			}
			if (product.isApprovalQueue()) {
				product.setApproved(false);
				ApprovalDetails approvalDetails = new ApprovalDetails();
				approvalDetails.setProduct(product);
				approvalDetails.setApprovalReqMsg("Price is more than $5,000, the product price need approval");
				product.setApprovalDetails(approvalDetails);
				productRepository.save(product);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (product.isApprovalQueue()) {
			return new ResponseEntity<>("Price is more than $5,000, Product pushed to Approval queue", HttpStatus.OK);
		}
		return new ResponseEntity<>("Product saved successfully.", HttpStatus.CREATED);
	}
	
	
	public ResponseEntity<?> updateProduct(Product product, Long id) {

		try {
			Optional<Product> pt = productRepository.findById(id);
			Product p = pt.get();
			int val = p.getPrice() * (50 / 100);

			p.setApprovalQueue(product.getPrice() < val ? true : false);
			p.setPrice(product.getPrice());
			p.setActive(product.isActive());
			p.setProductName(product.getProductName());
			p.setPostedDate(product.getPostedDate());

			if (!product.isApprovalQueue()) {
				productRepository.save(p);
			}
			if (product.isApprovalQueue()) {
				p.setApproved(false);

				ApprovalDetails a = new ApprovalDetails();
				a.setProduct(p);

				a.setApprovalReqMsg("Price is more than $5,000, Product pushed to Approval queue");
				p.setApprovalDetails(a);
				productRepository.save(p);
			}

		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (product.isApprovalQueue()) {
			return new ResponseEntity<>("Product is Appoval State Since Price is more than 50 percent",
					HttpStatus.OK);
		}
		return new ResponseEntity<>("Product updated Successfully", HttpStatus.OK);

	}
	
	
	public ResponseEntity<?> deleteProduct(Long productId) {
		try {
			Optional<Product> p1 = productRepository.findById(productId);
			Product p = p1.get();
			if (p.isApprovalQueue()) {
				return new ResponseEntity<>("Product Can not Delete, Which is in Approval Queue", HttpStatus.CONFLICT);
			}
			productRepository.deleteById(productId);

			ApprovalDetails a = new ApprovalDetails();
			a.setApprovalReqMsg("product Deleted, Adding in to Approval Queue productId:" + productId);

			approvalDetailsRepository.save(a);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>("Product deleted Successfully with ProductId" + productId, HttpStatus.OK);
	}
	
	public ResponseEntity<?> getApprovalQueueProducts() {
		List<ApprovalDetails> approvalQueueList = new ArrayList<>();
		try {
			List<Product> productlist = productRepository.findAllByOrderByPostedDateAsc();
			productlist = productlist.stream().filter(p -> p.isApprovalQueue() == true && p.getApprovalDetails() != null
					&& p.getApprovalDetails().getId() != null).collect(Collectors.toList());
			productlist.forEach(p -> {
				Optional<ApprovalDetails> a = approvalDetailsRepository.findById(p.getApprovalDetails().getId());
				ApprovalDetails a1 = a.get();
				approvalQueueList.add(a1);
			});
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(approvalQueueList, HttpStatus.OK);

	}
	
	public ResponseEntity<?> approveProducts(Long approvalId) {
		ApprovalDetails approvalQueue = null;
		try {

			Optional<ApprovalDetails> approvalQueues = approvalDetailsRepository.findById(approvalId);
			approvalQueue = approvalQueues.get();
			Product p = approvalQueue.getProduct();

			p.setApproved(true);
			p.setApprovalQueue(false);
			p.setApprovalDetails(null);
			productRepository.save(p);
			approvalDetailsRepository.deleteById(approvalId);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(
				"Product Approved Successfully and Deleting the approvalId " + approvalId + "from Queue",
				HttpStatus.OK);
	}
	
	public ResponseEntity<?> rejectProducts(Long approvalId) {
		ApprovalDetails approvalQueue = null;
		try {

			Optional<ApprovalDetails> approvalQueues = approvalDetailsRepository.findById(approvalId);
			approvalQueue = approvalQueues.get();
			Product p = approvalQueue.getProduct();
			p.setApproved(false);
			p.setApprovalQueue(false);
			p.setApprovalDetails(null);
			approvalDetailsRepository.deleteById(approvalId);
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>("Rejected the Approval and Deleted from Queue", HttpStatus.OK);
	}
	
}