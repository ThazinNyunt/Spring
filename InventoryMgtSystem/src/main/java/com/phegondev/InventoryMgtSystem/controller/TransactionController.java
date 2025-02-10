package com.phegondev.InventoryMgtSystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.dtos.TransactionRequest;
import com.phegondev.InventoryMgtSystem.enums.TransactionStatus;
import com.phegondev.InventoryMgtSystem.services.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;
	
	@PostMapping("/purchase")
    public ResponseEntity<Response> purchaseInventory(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.purchase(transactionRequest));
    }
	
	@PostMapping("/sell")
    public ResponseEntity<Response> makeSale(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.sell(transactionRequest));
    }
	
	@PostMapping("/return")
    public ResponseEntity<Response> returnToSupplier(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.returnToSupplier(transactionRequest));
    }
	
	@GetMapping("/all")
    public ResponseEntity<Response> getAllTransactions(
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "1000") int size, 
    		@RequestParam(required = false) String searchValue) {
		
        return ResponseEntity.ok(transactionService.getAllTransactions(page, size, searchValue));
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Response> getTransactionById(@PathVariable Long id) {
		
        return ResponseEntity.ok(transactionService.getAllTransactionById(id));
    }
	
	@GetMapping("/by-month-year")
    public ResponseEntity<Response> getTransactionByMonthAndYear(
    		@RequestParam int month,
    		@RequestParam int year) {
		
        return ResponseEntity.ok(transactionService.getAllTransactionByMonthAndYear(month, year));
    }
	
	@PutMapping("/{transactionId}")
    public ResponseEntity<Response> updateTransactionStatus(
    		@PathVariable Long transactionId,    		
    		@RequestBody TransactionStatus status) {
		
        return ResponseEntity.ok(transactionService.updateTransactionStatus(transactionId, status));
    }
	
	
}
