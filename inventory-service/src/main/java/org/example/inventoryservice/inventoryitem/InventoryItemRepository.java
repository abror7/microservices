package org.example.inventoryservice.inventoryitem;


import org.example.inventoryservice.inventoryitem.dto.InventoryItemDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryItemRepository extends ReactiveCrudRepository<InventoryItem, Integer> {

    Flux<InventoryItem> findAllByInventoryId(Integer inventoryId);

    String SELECT_QUERY = """
            SELECT ii.id as id,
                   ii.inventory_number as inventory_number,
                   ii.room_id as room_id,
                   ii.inventory_id as inventory_id,
                   i.name as inventory_name
            FROM inventory_item ii
                     LEFT JOIN inventory i ON i.id = ii.inventory_id
            where ii.id = :id
            """;

    @Query(SELECT_QUERY)
    Mono<InventoryItemDto> getInventoryItemById(Integer id);

    // TODO: 01/02/23 Refactor
    @Query("""
            SELECT ii.id as id,
                   ii.inventory_number as inventory_number,
                   ii.room_id as room_id,
                   ii.inventory_id as inventory_id,
                   i.name as inventory_name
            FROM inventory_item ii
                     LEFT JOIN inventory i ON i.id = ii.inventory_id
            where ii.room_id is not null and i.inventory_sign = :inventorySign and ii.inventory_number = :inventoryNumber
            offset (:page * :size) limit :size
            """)
    Flux<InventoryItemDto> findByInventorySignAndNumber(String inventorySign, Integer inventoryNumber, int page, int size);

    Mono<Integer> countByInventoryId(Integer inventoryId);
}
