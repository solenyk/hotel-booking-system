package com.kopchak.booking.service;

import com.kopchak.booking.domain.Booking;
import com.kopchak.booking.domain.HotelUser;
import com.kopchak.booking.domain.RoomAvailability;
import com.kopchak.booking.domain.RoomAvailabilityStatus;
import com.kopchak.booking.dto.BookingDTO;
import com.kopchak.booking.repository.BookingRepository;
import com.kopchak.booking.repository.HotelUserRepository;
import com.kopchak.booking.repository.RoomAvailabilityRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final HotelUserRepository hotelUserRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void book(Integer roomId, HotelUser user, BookingDTO bookingDTO) {
        long dateRangeSize = bookingDTO.startDate().datesUntil(bookingDTO.endDate().plusDays(1)).count();
        if (dateRangeSize >= 30) {
            throw new RuntimeException("Maximum booking days number is 30");
        }
        List<RoomAvailability> availabilities = roomAvailabilityRepository
                .findRoomAvailabilityInDateRange(bookingDTO.startDate(), bookingDTO.endDate(), roomId);
        if (availabilities.size() != dateRangeSize) {
            throw new RuntimeException("Some dates in the range are unavailable");
        }
        HotelUser savedUser = hotelUserRepository.save(user);
        Booking booking = Booking.builder()
                .roomId(roomId)
                .startDate(bookingDTO.startDate())
                .endDate(bookingDTO.endDate())
                .user(savedUser)
                .build();
        bookingRepository.save(booking);
        availabilities.forEach(availability -> availability.setStatus(RoomAvailabilityStatus.HOLD));
    }
}
