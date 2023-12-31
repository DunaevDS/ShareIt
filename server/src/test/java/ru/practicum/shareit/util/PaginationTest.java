package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {
    @Test
    public void test_SizeNotPositive() {
        Integer from = 0;
        Integer size = -1;
        assertThrows(BadRequestException.class, () -> new Pagination(from, size));
    }

    @Test
    public void test_FromNotPositive() {
        Integer from = -1;
        Integer size = 1;
        assertThrows(BadRequestException.class, () -> new Pagination(from, size));
    }

    @Test
    public void test_SizeLessThanFrom() {
        Integer from = 10;
        Integer size = 2;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(10);
        assertThat(pager.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void test_SizeMoreThanFrom() {
        Integer from = 2;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(2);
        assertThat(pager.getTotalPages()).isEqualTo(4);
    }

    @Test
    public void test_SizeEqualsFrom() {
        Integer from = 5;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(1);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void test_FromIsZeroAndSizeNotNull() {
        Integer from = 0;
        Integer size = 5;
        Pagination pager = new Pagination(from, size);
        assertThat(pager.getIndex()).isEqualTo(0);
        assertThat(pager.getPageSize()).isEqualTo(5);
        assertThat(pager.getTotalPages()).isEqualTo(1);
    }
}
