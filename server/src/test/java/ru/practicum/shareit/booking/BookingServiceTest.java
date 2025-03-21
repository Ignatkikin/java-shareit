package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownBookingStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;


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
                .owner(owner)
                .itemRequest(null)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 3, 10, 5, 0))
                .end(LocalDateTime.of(2025, 3, 15, 5, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        requestBookingDto = RequestBookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2025, 3, 10, 5, 0))
                .end(LocalDateTime.of(2025, 3, 15, 5, 0))
                .bookerId(1L)
                .status("WAITING")
                .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.createBooking(1L, requestBookingDto);
        assertNotNull(bookingDto);
        assertEquals(requestBookingDto.getItemId(), bookingDto.getItem().getId());
        assertEquals(requestBookingDto.getStart(), bookingDto.getStart());
        assertEquals(requestBookingDto.getEnd(), bookingDto.getEnd());
        assertEquals(requestBookingDto.getBookerId(), bookingDto.getBooker().getId());
    }


    @Test
    void createBookingWithNotFoundExceptionTest() {
        item.setOwner(user);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(1L, requestBookingDto));

        assertNotNull(e);
    }

    @Test
    void createBookingWithBookingValidationExceptionTest() {
        item.setAvailable(false);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        BookingValidationException e = assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(1L, requestBookingDto));

        assertNotNull(e);
    }

    @Test
    void createBookingWithBadDateRequest() {
        requestBookingDto.setEnd(LocalDateTime.of(2024, 3, 20, 5, 0));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        BookingValidationException e = assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(1L, requestBookingDto));

        assertNotNull(e);
    }

    @Test
    void updateBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.updateBooking(2L, 1L, true);

        assertNotNull(bookingDto);
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());

    }

    @Test
    void updateBookingWithAccessDeniedExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        AccessDeniedException e = assertThrows(AccessDeniedException.class,
                () -> bookingService.updateBooking(1L, 1L, true));

        assertNotNull(e);
    }

    @Test
    void updateBookingWithNotFoundExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(user);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, true));

        assertNotNull(e);
    }


    @Test
    void updateBookingWithBookingValidationExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        booking.setStatus(BookingStatus.APPROVED);

        BookingValidationException e = assertThrows(BookingValidationException.class,
                () -> bookingService.updateBooking(2L, 1L, true));

        assertNotNull(e);
    }

    @Test
    void getBookingByIdTest() {

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto bookingDto = bookingService.getBookingById(1L, 1L);

        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
    }

    @Test
    void getBookingByIdWithNotFoundExceptionTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(5L, 1L));

        assertNotNull(e);
    }

    @Test
    void getAllBookingsByUserIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllBookingsByUserId(1L, "ALL", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserIdWithUnknownStateExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UnknownBookingStateException e = assertThrows(UnknownBookingStateException.class,
                () -> bookingService.getAllBookingsByUserId(1L, "UNKNOWN", 0, 10));

        assertNotNull(e);
    }

    @Test
    void getAllBookingsByUserIdCurrentTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllBookingsByUserId(1L, "CURRENT", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingsByUserIdFutureTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllBookingsByUserId(1L, "FUTURE", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingsByUserIdWaitingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllBookingsByUserId(1L, "WAITING", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingsByUserIdRejectedTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllBookingsByUserId(1L, "REJECTED", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getAllBookingsByUserIdPastTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getAllBookingsByUserId(1L, "PAST", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(2L, "ALL", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getBookingsByOwnerIdWithUnknownStateExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        UnknownBookingStateException e = assertThrows(UnknownBookingStateException.class,
                () -> bookingService.getBookingsByOwnerId(2L, "UNKNOWN", 0, 10));

        assertNotNull(e);
    }

    @Test
    void getBookingsByOwnerId_CurrentTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(2L, "CURRENT", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerId_PastTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(2L, "PAST", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerId_FutureTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(2L, "FUTURE", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerId_WaitingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(2L, "WAITING", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwnerId_RejectedTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(2L, "REJECTED", 0, 10);

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }


}
