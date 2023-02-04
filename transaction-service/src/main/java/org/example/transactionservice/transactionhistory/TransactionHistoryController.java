package org.example.transactionservice.transactionhistory;

import lombok.RequiredArgsConstructor;
import org.example.transactionservice.payload.ApiResponse;
import org.example.transactionservice.transactionhistory.dto.TransactionHistoryDto;
import org.example.transactionservice.transactionhistory.dto.TransferInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionHistoryController {
    private final TransactionHistoryService transactionHistoryService;


    @GetMapping
    public Flux<TransactionHistoryDto> getAllTransactionHistory(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return transactionHistoryService.getAllTransactionHistory(page - 1, size);

    }

    @GetMapping("/{id}")
    public Mono<TransactionHistoryDto> getTransactionById(@PathVariable Integer id) {
        return transactionHistoryService.getTransactionById(id);
    }


    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> makeTransactionInventories(@RequestBody TransferInfoDto dto) {
        return transactionHistoryService.makeTransactionInventories(dto)
                .map(savedTransaction ->
                        new ResponseEntity<>(
                                new ApiResponse(true, "Transaction saved", savedTransaction),
                                HttpStatusCode.valueOf(201)
                        ));

    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> updateTransactionHistory(@PathVariable Integer id, @RequestBody TransactionHistoryDto dto) {
        return transactionHistoryService.updateTransactionHistory(id, dto)
                .map(updatedTransactionHistory ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully updated!!",
                                        updatedTransactionHistory),
                                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> deleteTransactionHistory(@PathVariable Integer id) {
        return transactionHistoryService.deleteTransaction(id)
                .map(deletedTransactionHistory ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully deleted!!",
                                        deletedTransactionHistory),
                                HttpStatus.OK));
    }
}
