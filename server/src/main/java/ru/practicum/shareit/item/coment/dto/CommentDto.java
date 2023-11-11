package ru.practicum.shareit.item.coment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Integer id;

    @NotBlank
    private String text;
    @JsonIgnore
    private Item item;
    private String authorName;

    private LocalDateTime created;
}
