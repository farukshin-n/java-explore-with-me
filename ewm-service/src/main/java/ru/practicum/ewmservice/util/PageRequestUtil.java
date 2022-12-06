package ru.practicum.ewmservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;

@UtilityClass
public class PageRequestUtil {
    public static PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
