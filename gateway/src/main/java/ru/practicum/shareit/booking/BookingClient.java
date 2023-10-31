package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(Integer userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsOwner(Integer userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBooking(Integer userId, Integer bookingId) {

        System.out.println("userId = " + userId);
        System.out.println("bookingId = " + bookingId);

        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> create(Integer userId, BookItemRequestDto requestDto) {
        System.out.println("userId = " + userId);
        System.out.println("requestDto = "+ requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> update(Integer bookingId, Integer userId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        System.out.println();
        System.out.println("userId = " + userId);
        System.out.println("bookingId = " + bookingId);
        System.out.println("approved = " + approved);

        return patch(path, userId, null, null);
    }
}
