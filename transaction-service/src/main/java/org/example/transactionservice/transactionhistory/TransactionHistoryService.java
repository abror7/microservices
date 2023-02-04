package org.example.transactionservice.transactionhistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.exception.CustomBadRequestException;
import org.example.transactionservice.exception.CustomGeneralException;
import org.example.transactionservice.exception.ResourceNotFoundException;
import org.example.transactionservice.transactionhistory.dto.TransactionHistoryDto;
import org.example.transactionservice.transactionhistory.dto.TransferInfoDto;
import org.example.transactionservice.transactionitem.TransactionItem;
import org.example.transactionservice.transactionitem.TransactionItemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionItemRepository transactionItemRepository;


    public Flux<TransactionHistoryDto> getAllTransactionHistory(int page, int size) {
        if (page < 0 || size <= 0) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return transactionHistoryRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(this::convertToTransactionDto);
    }


    public Mono<TransactionHistoryDto> getTransactionById(Integer id) {
        return transactionHistoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Transaction", id)))
                .map(this::convertToTransactionDto);

    }

    @Transactional
    public Mono<TransactionHistory> makeTransactionInventories(TransferInfoDto dto) {
        // TODO: 02/02/23 check buildings existence
        // Create a new transaction history and save it
        TransactionHistory transactionHistory = TransactionHistory.builder()
                .description(dto.getDescription())
                .fromBuildingId(dto.getFromBuildingId())
                .toBuildingId(dto.getToBuildingId())
                .build();
        return transactionHistoryRepository.save(transactionHistory)
                .flatMap(savedTransactionHistory -> {
                    // TODO: 02/02/23 check inventory existence and quantity

                    // For each inventory in the transfer, create a new transaction item
                    List<TransactionItem> transactionItems = dto.getInventories().stream()
                            .map(inventory -> TransactionItem.builder()
                                    .transactionHistoryId(savedTransactionHistory.getId())
                                    .inventoryId(inventory.getInventoryId())
                                    .quantity(inventory.getQuantity())
                                    .build())
                            .toList();
                    return transactionItemRepository.saveAll(transactionItems)
                            .then(Mono.just(savedTransactionHistory));
                });
    }


    public Mono<TransactionHistoryDto> updateTransactionHistory(Integer id, TransactionHistoryDto dto) {
        return transactionHistoryRepository.findById(id)
                .flatMap(existingTransactionHistory -> {
                    existingTransactionHistory.setFromBuildingId(dto.getFromBuildingId());
                    existingTransactionHistory.setToBuildingId(dto.getToBuildingId());
                    existingTransactionHistory.setDescription(dto.getDescription());
                    return transactionHistoryRepository.save(existingTransactionHistory);
                })
                .map(this::convertToTransactionDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("TransactionHistory", id)));
    }

    public Mono<Boolean> deleteTransaction(Integer id) {
        return transactionHistoryRepository.deleteById(id)
                .onErrorMap(e -> {
                    if (e instanceof DataIntegrityViolationException) {
                        return new CustomBadRequestException("You can't delete this transaction...");
                    }
                    return new CustomGeneralException();
                })
                .flatMap(unused -> Mono.just(true));
    }


    private TransactionHistoryDto convertToTransactionDto(TransactionHistory transactionHistory) {
        // TODO: 02/02/23 get buildings
        return TransactionHistoryDto.builder()
                .id(transactionHistory.getId())
                .fromBuildingId(transactionHistory.getFromBuildingId())
//                .fromBuildingName()
                .toBuildingId(transactionHistory.getToBuildingId())
//                .toBuildingName()
                .createdAt(transactionHistory.getCreatedAt())
                .updatedAt(transactionHistory.getUpdatedAt())
                .description(transactionHistory.getDescription())
                .build();
    }

}
