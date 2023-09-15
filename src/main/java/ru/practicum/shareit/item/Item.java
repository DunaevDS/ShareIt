package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {
    private int id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private int ownerId;
    private Integer requestId;
}
