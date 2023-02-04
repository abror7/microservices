package org.example.buildingservice.building;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends ReactiveCrudRepository<Building, Integer> {


}
