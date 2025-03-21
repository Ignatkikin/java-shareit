package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.InvalidPaginationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("RequestDesc")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("RequestDesc")
                .build();

        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .owner(user)
                .itemRequest(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDesc")
                .available(true)
                .build();
    }

    @Test
    void createRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());

    }

    @Test
    void createRequestWithNotFoundExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(1L, itemRequestDto));

        assertNotNull(e);

    }

    @Test
    void getRequestsByUserIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByItemRequestIdIn(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getRequestsByUserId(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRepository.findAllByItemRequestIdIn(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getRequests(user.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getRequestsWithInvalidPaginationTest() {
        InvalidPaginationException e = assertThrows(InvalidPaginationException.class,
                () -> itemRequestService.getRequests(user.getId(), -1, 10));

        assertNotNull(e);
    }

    @Test
    void getRequestByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(ofNullable(itemRequest));
        when(itemRepository.findAllByItemRequestId(anyLong()))
                .thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getRequestByIdWithNotFoundExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(user.getId(), 100L));

        assertNotNull(e);
    }

    @Test
    void getRequestByIdWithUserNotFoundExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(user.getId(), itemRequest.getId()));

        assertNotNull(e);
    }
}

