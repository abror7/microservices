package org.example.transactionservice.transactionhistory;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionHistoryRepository extends
        ReactiveCrudRepository<TransactionHistory, Integer> {
}
