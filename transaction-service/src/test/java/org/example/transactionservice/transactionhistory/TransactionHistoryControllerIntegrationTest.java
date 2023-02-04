package org.example.transactionservice.transactionhistory;

import org.example.transactionservice.payload.ApiResponse;
import org.example.transactionservice.transactionhistory.dto.TransactionHistoryDto;
import org.example.transactionservice.transactionhistory.dto.TransferInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class TransactionHistoryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransactionHistoryService transactionHistoryService;

    @Test
    public void getAllTransactionHistorySuccessful() {
        TransactionHistoryDto transactionHistoryDto = TransactionHistoryDto.builder().build();

        BDDMockito.given(transactionHistoryService.getAllTransactionHistory(0, 10))
                .willReturn(Flux.just(transactionHistoryDto));

        webTestClient.get().uri("/api/v1/transactions?page=1&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionHistoryDto.class)
                .hasSize(1)
                .contains(transactionHistoryDto);
    }

    @Test
    public void getTransactionByIdSuccessful() {
        Integer id = 1;
        TransactionHistoryDto transactionHistoryDto = TransactionHistoryDto.builder().build();

        BDDMockito.given(transactionHistoryService.getTransactionById(id))
                .willReturn(Mono.just(transactionHistoryDto));

        webTestClient.get().uri("/api/v1/transactions/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionHistoryDto.class)
                .isEqualTo(transactionHistoryDto);
    }

    @Test
    public void makeTransactionInventoriesSuccessful() {
        TransactionHistory transactionHistory = TransactionHistory.builder()
                .id(1)
                .description("Test")
                .build();
        TransferInfoDto dto = TransferInfoDto.builder().build();
        TransactionHistoryDto transactionHistoryDto = TransactionHistoryDto.builder().build();

        BDDMockito.given(transactionHistoryService.makeTransactionInventories(dto))
                .willReturn(Mono.just(transactionHistory));

        webTestClient.post().uri("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionHistoryDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertTrue(apiResponse.isSuccess());
                    assertEquals("Transaction saved", apiResponse.getMessage());
                    assertNotNull(apiResponse.getData());
                });
    }

    @Test
    public void updateTransactionHistorySuccessful() {
        Integer id = 1;
        TransactionHistoryDto dto = TransactionHistoryDto.builder().build();
        TransactionHistoryDto transactionHistoryDto = TransactionHistoryDto.builder().build();

        BDDMockito.given(transactionHistoryService.updateTransactionHistory(id, dto))
                .willReturn(Mono.just(transactionHistoryDto));

        webTestClient.put().uri("/api/v1/transactions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertTrue(apiResponse.isSuccess());
                    assertEquals("Successfully updated!!", apiResponse.getMessage());
                    assertNotNull(apiResponse.getData());
                });
        BDDMockito.verify(transactionHistoryService, VerificationModeFactory.times(1)).updateTransactionHistory(id, dto);
    }

    @Test
    public void deleteTransactionHistorySuccessful() {
        Integer id = 1;
        BDDMockito.given(transactionHistoryService.deleteTransaction(id))
                .willReturn(Mono.just(true));
        webTestClient.delete().uri("/api/v1/transactions/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertTrue(apiResponse.isSuccess());
                    assertEquals("Successfully deleted!!", apiResponse.getMessage());
                    assertNotNull(apiResponse.getData());
                });
        BDDMockito.verify(transactionHistoryService, VerificationModeFactory.times(1)).deleteTransaction(id);
    }
}
