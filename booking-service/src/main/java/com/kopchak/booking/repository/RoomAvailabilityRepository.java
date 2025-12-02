package com.kopchak.booking.repository;

import com.kopchak.booking.domain.RoomAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Integer> {

    @Query("SELECT MAX(ra.date) FROM RoomAvailability ra WHERE ra.roomId = :roomId")
    LocalDate findLastDateByRoomId(@Param("roomId") Integer roomId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT ra FROM RoomAvailability ra " +
            "WHERE ra.date >= :dateStart AND ra.date <= :dateEnd AND ra.roomId = :roomId AND ra.status = 'FREE'")
    List<RoomAvailability> findRoomAvailabilityInDateRange(LocalDate dateStart, LocalDate dateEnd, Integer roomId);
}
