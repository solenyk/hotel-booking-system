package com.kopchak.booking.job;

import com.kopchak.booking.domain.Room;
import com.kopchak.booking.domain.RoomAvailability;
import com.kopchak.booking.domain.RoomAvailabilityStatus;
import com.kopchak.booking.repository.RoomAvailabilityRepository;
import com.kopchak.booking.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityJobService {

    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    @Transactional
    @Job(name = "Generate inventory for next month", retries = 5)
    public void ensureInventoryExists() {
        LocalDate today = LocalDate.now();
        LocalDate lastAvailabilityDate = today.plusDays(31);

        List<Room> rooms = roomRepository.findAll();
        List<RoomAvailability> roomAvailabilities = new ArrayList<>();

        rooms.forEach(room -> {
            LocalDate lastDate = roomAvailabilityRepository.findLastDateByRoomId(room.getId());
            lastDate = lastDate != null ? lastDate : today;
            lastDate.datesUntil(lastAvailabilityDate).forEach(date -> {
                        RoomAvailability roomAvailability = RoomAvailability.builder()
                                .roomId(room.getId())
                                .date(date)
                                .version(0)
                                .status(RoomAvailabilityStatus.FREE)
                                .build();
                        roomAvailabilities.add(roomAvailability);
                    }
            );
        });

        roomAvailabilityRepository.saveAll(roomAvailabilities);
    }
}