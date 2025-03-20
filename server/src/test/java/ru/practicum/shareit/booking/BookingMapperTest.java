package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingMapperTest {

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private RequestBookingDto requestBookingDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("OwnerName")
                .email("OwnerEmail@gmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(user)
                .itemRequest(null)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 3, 10, 5, 0))
                .end(LocalDateTime.of(2025, 3, 15, 5, 0))
                .item(item)
                .booker(owner)
                .status(BookingStatus.APPROVED)
                .build();

        requestBookingDto = RequestBookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2025, 3, 10, 5, 0))
                .end(LocalDateTime.of(2025, 3, 15, 5, 0))
                .bookerId(2L)
                .status("WAITING")
                .build();
    }

    @Test
    void toBookingDtoTest() {
        BookingDto result = BookingMapper.toBookingDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void toBookingTest() {
        Booking result = BookingMapper.toBooking(requestBookingDto, item, owner);
        assertNotNull(result);
        assertEquals(requestBookingDto.getId(), result.getId());
        assertEquals(requestBookingDto.getStart(), result.getStart());
        assertEquals(requestBookingDto.getEnd(), result.getEnd());
        assertEquals(requestBookingDto.getBookerId(), result.getBooker().getId());
        assertEquals(requestBookingDto.getItemId(), result.getItem().getId());
        assertEquals(requestBookingDto.getStatus(), String.valueOf(result.getStatus()));
    }

    @Test
    void toBookingDtoForItemTest() {
        BookingDtoForItem result = BookingMapper.toBookingDtoForItem(booking);
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
        assertEquals(booking.getItem().getId(), result.getItemId());
        assertEquals(String.valueOf(booking.getStatus()), result.getStatus());

    }
}
