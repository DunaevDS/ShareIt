package ru.practicum.shareit.item.coment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.coment.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
