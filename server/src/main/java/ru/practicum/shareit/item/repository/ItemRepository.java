package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i " +
            "from Item as i " +
            "where (i.name ilike concat('%', ?1, '%') or i.description ilike concat('%', ?1, '%')) " +
            "and i.available = true")
    List<Item> searchByText(String text);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByItemRequestId(Long requestId);

    List<Item> findAllByItemRequestIdIn(List<Long> requestIds);
}
