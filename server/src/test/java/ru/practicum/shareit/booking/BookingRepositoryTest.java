package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void init() {
        user = User.builder()
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();

        owner = User.builder()
                .name("OwnerName")
                .email("OwnerEmail@gmail.com")
                .build();

        item = Item.builder()
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.of(2025, 3, 10, 5, 0))
                .end(LocalDateTime.of(2025, 3, 15, 5, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void findByBookerIdTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(
                user.getId(), Pageable.ofSize(10));

        assertEquals(1, bookings.getContent().size());
    }

    @Test
    void findByBookerIdAndStatusTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findByBookerIdAndStatus(
                user.getId(), BookingStatus.APPROVED, Pageable.ofSize(10));

        assertEquals(1, bookings.getContent().size());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        LocalDateTime now = LocalDateTime.of(2025, 3, 12, 5, 0);

        Page<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), now, now, Pageable.ofSize(10));

        assertEquals(1, bookings.getContent().size());
    }

    @Test
    void findByBookerIdAndEndBeforeTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        LocalDateTime now = LocalDateTime.of(2025, 3, 16, 5, 0);

        Page<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                user.getId(), now, Pageable.ofSize(10));

        assertEquals(1, bookings.getContent().size());
    }

    @Test
    void findByBookerIdAndStartAfterTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        LocalDateTime now = LocalDateTime.of(2025, 3, 8, 5, 0);

        Page<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                user.getId(), now, Pageable.ofSize(10));

        assertEquals(1, bookings.getContent().size());
    }

    @Test
    void findByItemOwnerIdTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(
                owner.getId(), Pageable.ofSize(10));

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndStatusTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatus(
                owner.getId(), BookingStatus.APPROVED, Pageable.ofSize(10));

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        LocalDateTime now = LocalDateTime.of(2025, 3, 12, 5, 0);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                owner.getId(), now, now, Pageable.ofSize(10));

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        LocalDateTime now = LocalDateTime.of(2025, 3, 16, 5, 0);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                owner.getId(), now, Pageable.ofSize(10));

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndStartAfterTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        LocalDateTime now = LocalDateTime.of(2025, 3, 8, 5, 0);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                owner.getId(), now, Pageable.ofSize(10));

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemAndStatusOrderByStartTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItemAndStatusOrderByStart(
                item, BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItemInAndStatusOrderByStartAscTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(
                List.of(item), BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
    }

    @Test
    void findBookingForCommentTest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingForComment(
                item.getId(), user.getId(), LocalDateTime.of(2025, 3, 16, 5, 0));

        assertEquals(1, bookings.size());
    }
}
