package org.example.buildingservice.room;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoomRepository extends ReactiveCrudRepository<Room, Integer> {

    String SELECT_QUERY = """
            SELECT r.id as id,
                   r.name as name,
                   r.area as area,
                   r.floor as floor,
                   r.building_id as building_id,
                   b.name as building_name
            FROM room r
                     LEFT JOIN building b ON b.id = r.building_id
            where r.id = :id
            """;

    @Query(SELECT_QUERY)
    Mono<RoomDto> getRoomById(Integer id);

    @Query("""
            SELECT sum(area) from room r where r.building_id = :buildingId
            """)
    Mono<Double> getAreaAllRoomsByBuildingId(Integer buildingId);

}
