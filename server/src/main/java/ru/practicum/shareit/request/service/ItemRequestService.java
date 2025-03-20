package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequestsByUserId(Long userId);

    List<ItemRequestDto> getRequests(Long userId, Integer from, Integer size);

    public ItemRequestDto getRequestById(Long userId, Long requestId);
}
