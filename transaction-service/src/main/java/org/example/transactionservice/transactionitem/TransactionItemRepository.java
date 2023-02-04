package org.example.transactionservice.transactionitem;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionItemRepository extends ReactiveCrudRepository<TransactionItem, Integer> {


    Flux<TransactionItem> findAllByTransactionHistoryId(Integer transactionHistoryId);
}
