package com.kopchak.booking.repository;

import com.kopchak.booking.domain.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Integer> {

    @Query("SELECT MAX(ra.date) FROM RoomAvailability ra WHERE ra.roomId = :roomId")
    LocalDate findLastDateByRoomId(@Param("roomId") Integer roomId);
}
