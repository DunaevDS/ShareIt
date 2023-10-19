package ru.practicum.shareit.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.BadRequestException;

@Getter
@Slf4j
public class Pagination {
    private Integer pageSize;
    private Integer index;
    private Integer totalPages;


    public Pagination(Integer from, Integer size) {
        if (size != null) {
            if ((from < 0) || (size < 0)) {
                log.error("ConflictException: values from ={}, size={} can not be less than 0", from, size);
                throw new BadRequestException("values from=" + from + " , size=" + size + " can not be less than 0");
            }
            if (size.equals(0)) {
                log.error("ConflictException: value size={} should be more than 0", size);
                throw new BadRequestException("value size=" + size + " should be more than 0");
            }
        }
        pageSize = from;
        index = 1;
        totalPages = 0;
        if (size == null) {
            if (from.equals(0)) {
                pageSize = 1000;
                index = 0;
            }
        } else {
            if (from.equals(size)) {
                pageSize = size;
            }
            if (from.equals(0)) {
                pageSize = size;
                index = 0;
            }
            totalPages = index + 1;
            if ((from < size) && (!from.equals(0))) {
                totalPages = size / from + index;
                if (size % from != 0) {
                    totalPages++;
                }
            }
        }
    }
}
