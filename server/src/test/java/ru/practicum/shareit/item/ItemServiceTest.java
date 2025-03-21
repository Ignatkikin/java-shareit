package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemDto itemDtoWithOutRequest;
    private ItemWithBookingDto itemWithBookingDto;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Item itemWithOutRequest;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private Booking booking1;
    private Booking booking2;

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

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("RequestDesc")
                .requester(user)
                .build();

        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(owner)
                .itemRequest(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .requestId(1L)
                .build();

        itemWithOutRequest = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(owner)
                .build();

        itemDtoWithOutRequest = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("CommentText")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        itemWithBookingDto = ItemWithBookingDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("CommentText")
                .itemId(item.getId())
                .authorName(user.getName())
                .created(LocalDateTime.now())
                .build();

        lastBooking = BookingDtoForItem.builder()
                .id(1L)
                .itemId(item.getId())
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .bookerId(user.getId())
                .status("APPROVED")
                .build();

        nextBooking = BookingDtoForItem.builder()
                .id(2L)
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .bookerId(user.getId())
                .status("APPROVED")
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .item(item)
                .start(lastBooking.getStart())
                .end(lastBooking.getEnd())
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        booking2 = Booking.builder()
                .id(2L)
                .item(item)
                .start(nextBooking.getStart())
                .end(nextBooking.getEnd())
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(owner));
        when(itemRepository.save(any()))
                .thenReturn(itemWithOutRequest);
        ItemDto result = itemService.createItem(owner.getId(), itemDtoWithOutRequest);

        assertNotNull(result);
        assertEquals(itemDtoWithOutRequest.getName(), result.getName());
        assertEquals(itemDtoWithOutRequest.getDescription(), result.getDescription());
        assertEquals(itemDtoWithOutRequest.getAvailable(), result.getAvailable());
        assertEquals(itemDtoWithOutRequest.getRequestId(), result.getRequestId());
    }

    @Test
    void createItemWithRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(owner));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(ofNullable(itemRequest));
        when(itemRepository.save(any()))
                .thenReturn(item);
        ItemDto result = itemService.createItem(owner.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    void createItemWithNonExistentUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.createItem(3L, itemDtoWithOutRequest));
        assertNotNull(e);
    }

    @Test
    void createItemWithNonExistentRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(owner));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.createItem(owner.getId(), itemDto));
        assertNotNull(e);
    }

    @Test
    void updateItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(item);
        itemDtoWithOutRequest.setDescription("NewDesc");
        ItemDto result = itemService.updateItem(owner.getId(), item.getId(), itemDtoWithOutRequest);
        assertNotNull(result);
        assertEquals(itemDtoWithOutRequest.getDescription(), result.getDescription());
        assertEquals(itemDtoWithOutRequest.getId(), result.getId());
    }

    @Test
    void updateItemWithNullFieldsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto newItemDto = ItemDto.builder().build();
        ItemDto result = itemService.updateItem(owner.getId(), item.getId(), newItemDto);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void updateItemWithNotFoundUserTest() {
        itemDtoWithOutRequest.setDescription("NewDesc");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemDtoWithOutRequest));
        assertNotNull(e);
    }

    @Test
    void updateItemWithNotFoundItemTest() {
        itemDtoWithOutRequest.setDescription("NewDesc");
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemDtoWithOutRequest));
        assertNotNull(e);
    }

    @Test
    void updateItemWithAccessDeniedExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        AccessDeniedException e = assertThrows(AccessDeniedException.class,
                () -> itemService.updateItem(user.getId(), item.getId(), itemDtoWithOutRequest));
        assertNotNull(e);
    }

    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(bookingRepository.findByItemAndStatusOrderByStart(any(), any()))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of(comment));
        ItemWithBookingDto result = itemService.getItemById(item.getId(), owner.getId());
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(1, result.getComments().size());
        assertEquals(commentDto.getText(), result.getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.getComments().get(0).getAuthorName());
    }

    @Test
    void getItemByIdWithUserIsNotOwnerTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(bookingRepository.findByItemAndStatusOrderByStart(any(), any()))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemWithBookingDto result = itemService.getItemById(item.getId(), 10L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void getItemByIdWithNotFoundItemTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1L, 1L));

        assertNotNull(e);
    }

    @Test
    void getItemByIdWithBookingsTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(bookingRepository.findByItemAndStatusOrderByStart(any(), any()))
                .thenReturn(List.of(booking1, booking2));
        when(commentRepository.findByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemWithBookingDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(booking1.getId(), result.getLastBooking().getId());
        assertEquals(booking2.getId(), result.getNextBooking().getId());

        assertEquals(1, result.getComments().size());
        assertEquals(commentDto.getText(), result.getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.getComments().get(0).getAuthorName());
    }

    @Test
    void getItemsByUserIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(anyList(), any()))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemIn(anyList()))
                .thenReturn(List.of(comment));

        List<ItemWithBookingDto> result = itemService.getItemsByUserId(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
        assertEquals(1, result.get(0).getComments().size());
        assertEquals(commentDto.getText(), result.get(0).getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.get(0).getComments().get(0).getAuthorName());
    }

    @Test
    void getItemsByUserIdWithBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(anyList(), any()))
                .thenReturn(List.of(booking1, booking2));
        when(commentRepository.findByItemIn(anyList()))
                .thenReturn(List.of(comment));

        List<ItemWithBookingDto> result = itemService.getItemsByUserId(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());

        assertNotNull(result.get(0).getLastBooking());
        assertEquals(lastBooking.getId(), result.get(0).getLastBooking().getId());
        assertNotNull(result.get(0).getNextBooking());
        assertEquals(nextBooking.getId(), result.get(0).getNextBooking().getId());

        assertEquals(1, result.get(0).getComments().size());
        assertEquals(commentDto.getText(), result.get(0).getComments().get(0).getText());
        assertEquals(commentDto.getAuthorName(), result.get(0).getComments().get(0).getAuthorName());
    }

    @Test
    void getItemsByUserIdWithNotFoundUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItemsByUserId(3L));
        assertNotNull(e);
    }

    @Test
    void addCommentTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(bookingRepository.findBookingForComment(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking1));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(commentDto.getAuthorName(), result.getAuthorName());
    }

    @Test
    void addCommentWithBookingValidationExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(ofNullable(item));
        when(bookingRepository.findBookingForComment(anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());

        BookingValidationException e = assertThrows(BookingValidationException.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDto));
        assertNotNull(e);
    }

    @Test
    void addCommentWithNotFoundUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.addComment(3L, item.getId(), commentDto));
        assertNotNull(e);
    }

    @Test
    void addCommentWithNotFoundItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.addComment(user.getId(), 3L, commentDto));
        assertNotNull(e);
    }

    @Test
    void getItemsByTextTest() {
        when(itemRepository.searchByText(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.getItemsByText("ItemName");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto.getName(), result.get(0).getName());
    }

    @Test
    void getItemsByTextWithNullTextTest() {
        List<ItemDto> result = itemService.getItemsByText(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsByTextWithEmptyTextTest() {
        List<ItemDto> result = itemService.getItemsByText("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
