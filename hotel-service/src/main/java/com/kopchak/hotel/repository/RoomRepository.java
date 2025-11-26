package com.kopchak.hotel.repository;

import com.kopchak.hotel.domain.Room;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer>, JpaSpecificationExecutor<Room> {

    @Override
    @EntityGraph(attributePaths = "photos")
    List<Room> findAll(Specification<Room> spec);
}
