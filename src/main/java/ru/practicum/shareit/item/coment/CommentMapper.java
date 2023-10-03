package ru.practicum.shareit.item.coment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.coment.model.Comment;

@Component
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
