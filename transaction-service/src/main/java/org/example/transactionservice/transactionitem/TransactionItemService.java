package org.example.transactionservice.transactionitem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.exception.CustomBadRequestException;
import org.example.transactionservice.exception.CustomGeneralException;
import org.example.transactionservice.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionItemService {
    private final TransactionItemRepository transactionItemRepository;


    public Flux<TransactionItemDto> getAllTransactionItem(int page, int size) {
        if (page < 0 || size <= 0) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return transactionItemRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(this::convertToTransactionDto);
    }

    public Flux<TransactionItemDto> getAllTransactionItemByHistoryId(Integer historyId, int page, int size) {
        if (page < 0 || size <= 0) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return transactionItemRepository.findAllByTransactionHistoryId(historyId)
                .skip((long) page * size)
                .take(size)
                .map(this::convertToTransactionDto);
    }


    public Mono<TransactionItemDto> getTransactionById(Integer id) {
        return transactionItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Transaction", id)))
                .map(this::convertToTransactionDto);

    }

    @Transactional
    public Mono<TransactionItem> addTransactionItem(TransactionItemDto dto) {
        // TODO: 02/02/23 check history existence
        TransactionItem transactionItem = TransactionItem.builder()
                .transactionHistoryId(dto.getTransactionHistoryId())
                .inventoryId(dto.getInventoryId())
                .quantity(dto.getQuantity())
                .inventoryId(dto.getInventoryId())
                .build();
        return transactionItemRepository.save(transactionItem);

    }


    public Mono<TransactionItemDto> updateTransactionItem(Integer id, TransactionItemDto dto) {
        return transactionItemRepository.findById(id)
                .flatMap(existingTransactionItem -> {
                    existingTransactionItem.setInventoryId(dto.getInventoryId());
                    existingTransactionItem.setQuantity(dto.getQuantity());
                    return transactionItemRepository.save(existingTransactionItem);
                })
                .map(this::convertToTransactionDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("TransactionItem", id)));
    }

    public Mono<Void> deleteTransaction(Integer id) {

        return transactionItemRepository.deleteById(id)
                .onErrorMap(e -> {
                    if (e instanceof DataIntegrityViolationException) {
                        return new CustomBadRequestException("You can't delete this transaction...");
                    }
                    return new CustomGeneralException();
                });
    }


    private TransactionItemDto convertToTransactionDto(TransactionItem transactionItem) {
        // TODO: 02/02/23 get inventory
        return TransactionItemDto.builder()
                .id(transactionItem.getId())
                .inventoryId(transactionItem.getInventoryId())
                .quantity(transactionItem.getQuantity())
                .transactionHistoryId(transactionItem.getTransactionHistoryId())
//                .inventoryName()
//                .inventorySign()
                .build();
    }

}
