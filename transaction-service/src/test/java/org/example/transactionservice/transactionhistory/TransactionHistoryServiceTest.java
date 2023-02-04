package org.example.transactionservice.transactionhistory;

import org.example.transactionservice.exception.CustomBadRequestException;
import org.example.transactionservice.exception.ResourceNotFoundException;
import org.example.transactionservice.transactionhistory.dto.TransactionHistoryDto;
import org.example.transactionservice.transactionhistory.dto.TransferInfoDto;
import org.example.transactionservice.transactionhistory.dto.TransferInventoryDto;
import org.example.transactionservice.transactionitem.TransactionItem;
import org.example.transactionservice.transactionitem.TransactionItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionHistoryServiceTest {

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private TransactionItemRepository transactionItemRepository;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    private TransactionHistory transactionHistory;
    private TransactionHistoryDto transactionHistoryDto;
    private TransferInfoDto transferInfoDto;
    private List<TransactionItem> transactionItems;

    @BeforeEach
    public void setUp() {
        transactionHistory = TransactionHistory.builder()
                .id(1)
                .description("Test transaction history")
                .fromBuildingId(1)
                .toBuildingId(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        transactionHistoryDto = TransactionHistoryDto.builder()
                .id(transactionHistory.getId())
                .description(transactionHistory.getDescription())
                .fromBuildingId(transactionHistory.getFromBuildingId())
                .toBuildingId(transactionHistory.getToBuildingId())
                .createdAt(transactionHistory.getCreatedAt())
                .updatedAt(transactionHistory.getUpdatedAt())
                .build();

        transferInfoDto = TransferInfoDto.builder()
                .description("Test transfer info")
                .fromBuildingId(1)
                .toBuildingId(2)
                .build();

        transactionItems = new ArrayList<>();
        transactionItems.add(TransactionItem.builder()
                .id(1)
                .transactionHistoryId(transactionHistory.getId())
                .inventoryId(1)
                .quantity(5)
                .build());
    }

    @Test
    void getAllTransactionHistoryShouldReturnTransactionHistoryDtoListWhenGivenValidPageAndSize() {
        when(transactionHistoryRepository.findAll())
                .thenReturn(Flux.just(transactionHistory));

        Flux<TransactionHistoryDto> result = transactionHistoryService.getAllTransactionHistory(0, 10);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void getAllTransactionHistoryShouldReturnErrorWhenGivenInvalidPageOrSize() {
        Flux<TransactionHistoryDto> result = transactionHistoryService.getAllTransactionHistory(-1, 0);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void getTransactionByIdShouldReturnTransactionHistoryDtoWhenGivenValidId() {
        when(transactionHistoryRepository.findById(1))
                .thenReturn(Mono.just(transactionHistory));

        Mono<TransactionHistoryDto> result = transactionHistoryService.getTransactionById(1);

        StepVerifier.create(result)
                .expectNext(transactionHistoryDto)
                .verifyComplete();
    }

    @Test
    void getTransactionByIdShouldReturnErrorWhenGivenInvalidId() {
        when(transactionHistoryRepository.findById(1))
                .thenReturn(Mono.empty());

        Mono<TransactionHistoryDto> result = transactionHistoryService.getTransactionById(1);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void makeTransactionInventoriesShouldReturnTransactionHistoryWhenGivenValidInput() {
        List<TransferInventoryDto> inventories = new ArrayList<>();
        TransferInfoDto transferInfoDto = TransferInfoDto.builder()
                .fromBuildingId(1)
                .toBuildingId(2)
                .description("Test Transaction")
                .inventories(inventories)
                .build();
        TransactionItem transactionItem = TransactionItem.builder()
                .id(1)
                .inventoryId(1)
                .transactionHistoryId(1)
                .quantity(10)
                .build();
        when(transactionHistoryRepository.save(any(TransactionHistory.class)))
                .thenReturn(Mono.just(transactionHistory));
        when(transactionItemRepository.saveAll(any(Iterable.class)))
                .thenReturn(Flux.just(transactionItem));

        Mono<TransactionHistory> result = transactionHistoryService.makeTransactionInventories(transferInfoDto);

        StepVerifier.create(result)
                .expectNext(transactionHistory)
                .verifyComplete();
    }

    @Test
    void updateTransactionHistoryShouldReturnTransactionHistoryDtoWhenGivenValidInput() {
        when(transactionHistoryRepository.findById(1))
                .thenReturn(Mono.just(transactionHistory));
        when(transactionHistoryRepository.save(any(TransactionHistory.class)))
                .thenReturn(Mono.just(transactionHistory));

        Mono<TransactionHistoryDto> result = transactionHistoryService.updateTransactionHistory(1, transactionHistoryDto);

        StepVerifier.create(result)
                .expectNext(transactionHistoryDto)
                .verifyComplete();
    }

    @Test
    void updateTransactionHistoryShouldReturnErrorWhenGivenInvalidId() {
        when(transactionHistoryRepository.findById(1))
                .thenReturn(Mono.empty());

        Mono<TransactionHistoryDto> result = transactionHistoryService.updateTransactionHistory(1, transactionHistoryDto);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTransactionShouldReturnTrueWhenGivenValidId() {
        when(transactionHistoryRepository.deleteById(1))
                .thenReturn(Mono.empty());

        Mono<Boolean> result = transactionHistoryService.deleteTransaction(1);

        StepVerifier.create(result)
                .expectNext()
                .expectComplete()
                .verify();
    }

    @Test
    void deleteTransactionShouldReturnErrorWhenGivenInvalidId() {
        when(transactionHistoryRepository.deleteById(1))
                .thenReturn(Mono.error(new DataIntegrityViolationException("Test")));
        Mono<Boolean> result = transactionHistoryService.deleteTransaction(1);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }


}

