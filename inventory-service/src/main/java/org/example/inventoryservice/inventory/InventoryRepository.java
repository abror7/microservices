package org.example.inventoryservice.inventory;


import org.example.inventoryservice.inventory.dto.InventoriesByRoomResponseDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface InventoryRepository extends ReactiveCrudRepository<Inventory, Integer> {

    @Query("""
            SELECT i.id        as id,
                   i.name,
                   i.inventory_sign,
                   count(i.id) as quantity
            from inventory i
                     join inventory_item ii on i.id = ii.inventory_id
            where ii.room_id = :roomId
            group by i.id, i.name, i.inventory_sign
            offset (:page * :size) limit :size
            """)
    Flux<InventoriesByRoomResponseDto> getInventoriesByRoomId(Integer roomId, int page, int size);
}
