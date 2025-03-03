package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public BookingDto createBooking(Long userId, RequestBookingDto requestBookingDto) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(requestBookingDto.getItemId()).orElseThrow(() -> new NotFoundException(
                "Item с id " + requestBookingDto.getItemId() + " не найден"));
        Booking booking = BookingMapper.toBooking(requestBookingDto, item, booker);
        if (!item.getAvailable()) {
            throw new BookingValidationException("Item не доступен для бронирования");
        }
        if (!requestBookingDto.getEnd().isAfter(requestBookingDto.getStart())) {
            throw new BookingValidationException("Дата окончания должна быть позже даты начала");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не может бранировать свой Item");
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                "Бронь с id " + bookingId + " не найдена"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException(
                "Item не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Пользователь с id " + userId + " не является владельцем Item с id " +
                    item.getId());
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingValidationException("Статус уже утвержден");
        }
        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(bookingStatus);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                "Бронь с id " + bookingId + " не найдена"));
        if (!userId.equals(booking.getItem().getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new NotFoundException("Пользователь с id " + userId + " не владеет Item с id " +
                    booking.getItem().getId());
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с id " + userId + " не найден"
        ));
        State bookingState = checkState(state);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList;

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(user.getId());
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        user.getId(), dateTime, dateTime);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), dateTime);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(user.getId(), dateTime);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.REJECTED);
                break;
            default:
                throw new UnknownBookingStateException("Неизвестный статус");

        }
        return bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с id " + userId + " не найден"
        ));
        State bookingState = checkState(state);
        List<Booking> bookingList;
        LocalDateTime dateTime = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId());
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        user.getId(), dateTime, dateTime);
                break;
            case PAST:
                bookingList = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(user.getId(), dateTime);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(user.getId(), dateTime);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItemOwnerIdAndStatus(user.getId(), BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItemOwnerIdAndStatus(user.getId(), BookingStatus.REJECTED);
                break;
            default:
                throw new UnknownBookingStateException("Неизвестный статус");
        }
        return bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new UnknownBookingStateException("Неизвестный статус " + state);
        }
    }
}
