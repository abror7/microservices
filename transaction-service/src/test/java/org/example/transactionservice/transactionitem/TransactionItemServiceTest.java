package org.example.transactionservice.transactionitem;

import org.example.transactionservice.exception.CustomBadRequestException;
import org.example.transactionservice.exception.ResourceNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionItemServiceTest {
    @Mock
    private TransactionItemRepository transactionItemRepository;

    @InjectMocks
    private TransactionItemService transactionItemService;

    private TransactionItem transactionItem;
    private TransactionItemDto transactionItemDto;

    @BeforeEach
    public void setUp() {
        transactionItem = TransactionItem.builder()
                .id(1)
                .inventoryId(2)
                .transactionHistoryId(1)
                .quantity(3)
                .build();

        transactionItemDto = TransactionItemDto.builder()
                .id(1)
                .transactionHistoryId(1)
                .inventoryId(2)
                .quantity(3)
                .build();
    }

    @Test
    void getAllTransactionItemShouldReturnTransactionItemDtoFluxWhenGivenValidPageAndSize() {
        when(transactionItemRepository.findAll())
                .thenReturn(Flux.just(transactionItem));

        Flux<TransactionItemDto> result = transactionItemService.getAllTransactionItem(0, 10);

        StepVerifier.create(result)
                .expectNext(transactionItemDto)
                .verifyComplete();
    }

    @Test
    void getAllTransactionItemShouldReturnCustomBadRequestExceptionWhenGivenInvalidPageOrSize() {
        Flux<TransactionItemDto> result = transactionItemService.getAllTransactionItem(-1, 0);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void getAllTransactionItemByHistoryIdShouldReturnTransactionItemDtoFluxWhenGivenValidHistoryIdPageAndSize() {
        when(transactionItemRepository.findAllByTransactionHistoryId(1))
                .thenReturn(Flux.just(transactionItem));

        Flux<TransactionItemDto> result = transactionItemService.getAllTransactionItemByHistoryId(1, 0, 1);

        StepVerifier.create(result)
                .expectNext(transactionItemDto)
                .verifyComplete();
    }

    @Test
    void getAllTransactionItemByHistoryIdShouldReturnCustomBadRequestExceptionWhenGivenInvalidPageOrSize() {
        Flux<TransactionItemDto> result = transactionItemService.getAllTransactionItemByHistoryId(1, -1, 0);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void getTransactionByIdShouldReturnTransactionItemDtoWhenGivenValidId() {
        when(transactionItemRepository.findById(1))
                .thenReturn(Mono.just(transactionItem));

        Mono<TransactionItemDto> result = transactionItemService.getTransactionById(1);

        StepVerifier.create(result)
                .expectNext(transactionItemDto)
                .verifyComplete();
    }

    @Test
    void getTransactionByIdShouldReturnErrorWhenGivenInvalidId() {
        when(transactionItemRepository.findById(1))
                .thenReturn(Mono.empty());
        Mono<TransactionItemDto> result = transactionItemService.getTransactionById(1);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void addTransactionShouldReturnTransactionItemWhenGivenValidTransactionItemDto() {

        when(transactionItemRepository.save(any(TransactionItem.class)))
                .thenReturn(Mono.just(transactionItem));

        Mono<TransactionItem> result = transactionItemService.addTransactionItem(transactionItemDto);

        StepVerifier.create(result)
                .expectNext(transactionItem)
                .verifyComplete();
    }


    @Test
    void updateTransactionShouldReturnTransactionItemDtoWhenGivenValidIdAndTransactionItemDto() {
        when(transactionItemRepository.findById(1))
                .thenReturn(Mono.just(transactionItem));
        when(transactionItemRepository.save(transactionItem))
                .thenReturn(Mono.just(transactionItem));

        Mono<TransactionItemDto> result = transactionItemService.updateTransactionItem(1, transactionItemDto);

        StepVerifier.create(result)
                .expectNext(transactionItemDto)
                .verifyComplete();
    }

    @Test
    void updateTransactionShouldReturnErrorWhenGivenInvalidId() {
        when(transactionItemRepository.findById(1))
                .thenReturn(Mono.empty());

        Mono<TransactionItemDto> result = transactionItemService.updateTransactionItem(1, transactionItemDto);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTransactionShouldReturnVoidWhenGivenValidId() {
        when(transactionItemRepository.deleteById(1))
                .thenReturn(Mono.empty());

        Mono<Void> result = transactionItemService.deleteTransaction(1);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void deleteTransactionShouldReturnErrorWhenGivenInvalidId() {
        when(transactionItemRepository.deleteById(1))
                .thenReturn(Mono.error(new DataIntegrityViolationException("Error")));

        Mono<Void> result = transactionItemService.deleteTransaction(1);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }
}
