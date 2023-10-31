package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Integer userId, ItemDto itemDto) {

        log.info("userId = " + userId);
        log.info("itemId = " + itemDto);

        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Integer userId, Integer itemId) {

        log.info("userId = " + userId);
        log.info("itemId = " + itemId);

        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        System.out.println("userId = " + userId);
        System.out.println("from = " + from);
        System.out.println("size = " + size);

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Integer itemId, Integer userId) {
        return patch("/" + itemId.longValue(), userId, itemDto);
    }

    public ResponseEntity<Object> delete(Integer itemId, Integer userId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsBySearchQuery(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Integer itemId, Integer userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}