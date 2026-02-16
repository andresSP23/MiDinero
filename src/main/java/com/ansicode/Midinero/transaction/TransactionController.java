package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.commom.PageResponse;
import com.ansicode.Midinero.enums.TransactionType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> create(
            @RequestBody @Valid TransactionRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(transactionService.createTransaction(request, connectedUser));
    }

    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<TransactionResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "type", required = false) TransactionType type,
            @ParameterObject Pageable pageable,
            Authentication connectedUser) {
        return ResponseEntity.ok(transactionService.findAllTransactionsByUser(page, size, type, connectedUser));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<TransactionResponse> findById(
            @PathVariable Long id,
            Authentication connectedUser) {
        return ResponseEntity.ok(transactionService.findTransactionById(id, connectedUser));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TransactionRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request, connectedUser));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication connectedUser) {
        transactionService.deleteTransaction(id, connectedUser);
        return ResponseEntity.noContent().build();
    }
}