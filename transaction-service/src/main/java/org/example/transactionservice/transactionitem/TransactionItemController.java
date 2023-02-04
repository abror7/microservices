package org.example.transactionservice.transactionitem;

import lombok.RequiredArgsConstructor;
import org.example.transactionservice.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transaction-items")
@RequiredArgsConstructor
public class TransactionItemController {
    private final TransactionItemService transactionItemService;


    @GetMapping
    public Flux<TransactionItemDto> getAllTransactionItem(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return transactionItemService.getAllTransactionItem(page - 1, size);

    }

    @GetMapping("/byHistoryId/{transactionHistoryId}")
    public Flux<TransactionItemDto> getAllTransactionItemByHistoryId(@PathVariable Integer transactionHistoryId,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return transactionItemService.getAllTransactionItemByHistoryId(transactionHistoryId, page - 1, size);

    }

    @GetMapping("/{id}")
    public Mono<TransactionItemDto> getTransactionById(@PathVariable Integer id) {
        return transactionItemService.getTransactionById(id);
    }


    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> addTransactionItem(@RequestBody TransactionItemDto dto) {
        return transactionItemService.addTransactionItem(dto)
                .map(savedTransaction ->
                        new ResponseEntity<>(
                                new ApiResponse(true, "Transaction Item saved", savedTransaction),
                                HttpStatusCode.valueOf(201)
                        ));

    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> updateTransactionItem(@PathVariable Integer id, @RequestBody TransactionItemDto dto) {
        return transactionItemService.updateTransactionItem(id, dto)
                .map(updatedTransactionItem ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully updated!!",
                                        updatedTransactionItem),
                                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> deleteTransactionItem(@PathVariable Integer id) {
        return transactionItemService.deleteTransaction(id)
                .map(deletedTransactionItem ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully deleted!!",
                                        deletedTransactionItem),
                                HttpStatus.OK));
    }
}
