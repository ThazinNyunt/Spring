package com.phegondev.InventoryMgtSystem.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.dtos.TransactionDTO;
import com.phegondev.InventoryMgtSystem.dtos.TransactionRequest;
import com.phegondev.InventoryMgtSystem.enums.TransactionStatus;
import com.phegondev.InventoryMgtSystem.enums.TransactionType;
import com.phegondev.InventoryMgtSystem.exceptions.NameValueRequiredException;
import com.phegondev.InventoryMgtSystem.exceptions.NotFoundException;
import com.phegondev.InventoryMgtSystem.models.Product;
import com.phegondev.InventoryMgtSystem.models.Supplier;
import com.phegondev.InventoryMgtSystem.models.Transaction;
import com.phegondev.InventoryMgtSystem.models.User;
import com.phegondev.InventoryMgtSystem.repositories.ProductRepository;
import com.phegondev.InventoryMgtSystem.repositories.SupplierRepository;
import com.phegondev.InventoryMgtSystem.repositories.TransactionRepository;
import com.phegondev.InventoryMgtSystem.services.TransactionService;
import com.phegondev.InventoryMgtSystem.services.UserService;
import com.phegondev.InventoryMgtSystem.specification.TransactionFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
	
	private final TransactionRepository transactionRepository;
	private final ProductRepository productRepository;
	private final SupplierRepository supplierRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;	
	
	@Override
	public Response purchase(TransactionRequest transactionRequest) {
		
		Long productId = transactionRequest.getProductId();
		Long supplierId = transactionRequest.getSupplierId();
		Integer quantity = transactionRequest.getQuantity();
		
		if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("Product Not Found"));
		
		Supplier supplier = supplierRepository.findById(supplierId)
				.orElseThrow(() -> new NotFoundException("Supplier Not Found"));
		
		User user = userService.getCurrentLoggedInUser();
		
		// update stock quantity and re-save
		product.setStockQuantiy(product.getStockQuantiy() + quantity);
		productRepository.save(product);
		
		// create transaction
		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.PURCHASE)
				.status(TransactionStatus.COMPLETED)
				.product(product)
				.user(user)
				.supplier(supplier)
				.totalProducts(quantity)
				.totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
				.description(transactionRequest.getDescription())
				.note(transactionRequest.getNote())
				.build();
		
		transactionRepository.save(transaction);
		
		return Response.builder()
				.status(200)
				.message("Purchase Made successfully.")
				.build();
	}

	@Override
	public Response sell(TransactionRequest transactionRequest) {
		
		Long productId = transactionRequest.getProductId();
		Integer quantity = transactionRequest.getQuantity();
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("Product Not Found"));
		
		User user = userService.getCurrentLoggedInUser();
		
		// update stock quantity and re-save
		product.setStockQuantiy(product.getStockQuantiy() - quantity);
		productRepository.save(product);
		
		// create transaction
		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.SALE)
				.status(TransactionStatus.COMPLETED)
				.product(product)
				.user(user)
				.totalProducts(quantity)
				.totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
				.description(transactionRequest.getDescription())
				.note(transactionRequest.getNote())
				.build();
		
		transactionRepository.save(transaction);
		
		return Response.builder()
				.status(200)
				.message("Product Sell successfully made.")
				.build();
		
	}

	@Override
	public Response returnToSupplier(TransactionRequest transactionRequest) {
		
		Long productId = transactionRequest.getProductId();
		Long supplierId = transactionRequest.getSupplierId();
		Integer quantity = transactionRequest.getQuantity();
		
		if (supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("Product Not Found"));
		
		Supplier supplier = supplierRepository.findById(supplierId)
				.orElseThrow(() -> new NotFoundException("Supplier Not Found"));
		
		User user = userService.getCurrentLoggedInUser();		

		// update stock quantity and re-save
		product.setStockQuantiy(product.getStockQuantiy() - quantity);
		productRepository.save(product);

		// create transaction
		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.RETURN_TO_SUPPLIER)
				.status(TransactionStatus.PROCESSING)
				.product(product)
				.user(user)
				.totalProducts(quantity)
				.totalPrice(BigDecimal.ZERO)
				.description(transactionRequest.getDescription())
				.note(transactionRequest.getNote())
				.build();
		
		transactionRepository.save(transaction);
		
		return Response.builder()
				.status(200)
				.message("Product Returned in progress.")
				.build();
	}

	@Override
	public Response getAllTransactions(int page, int size, String filter) {
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		
		// User the Transaction specification
		Specification<Transaction> spec = TransactionFilter.byFilter(filter);
		Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
		
		List<TransactionDTO> transactionDTOS = modelMapper.map(transactionPage.getContent(),
				new TypeToken<List<TransactionDTO>>() {}.getType());
		
		transactionDTOS.forEach(transactionDTO -> {
			transactionDTO.setUser(null);
			transactionDTO.setProduct(null);
			transactionDTO.setSupplier(null);
		});
		
		return Response.builder()
				.status(200)
				.message("success")
				.transactions(transactionDTOS)
				.totalElements(transactionPage.getTotalElements())
				.totalPages(transactionPage.getTotalPages())
				.build();
		
	}

	@Override
	public Response getAllTransactionById(Long id) {
		
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Transaction Not Found"));
		
		TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
		
		transactionDTO.setUser(null);
		
		return Response.builder()
				.status(200)
				.message("success")
				.transaction(transactionDTO)
				.build();
	}

	@Override
	public Response getAllTransactionByMonthAndYear(int month, int year) {
		List<Transaction> transactions = transactionRepository.findAll(TransactionFilter.byMonthAndYear(month, year));
		
		List<TransactionDTO> transactionDTOS = modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {}.getType());
		
		transactionDTOS.forEach(transactionDTO -> {
			transactionDTO.setUser(null);
			transactionDTO.setProduct(null);
			transactionDTO.setSupplier(null);
		});
		
		return Response.builder()
				.status(200)
				.message("success")
				.transactions(transactionDTOS)
				.build();
	}

	@Override
	public Response updateTransactionStatus(Long transactionId, TransactionStatus status) {
		
		Transaction existingTransaction = transactionRepository.findById(transactionId)
				.orElseThrow(() -> new NotFoundException("Transaction Not Found"));
		
		existingTransaction.setStatus(status);
		existingTransaction.setUpdatedAt(LocalDateTime.now());
		
		transactionRepository.save(existingTransaction);
		
		return Response.builder()
				.status(200)
				.message("Transaction Status Successsfully Updated.")
				.build();
		
	}

}
