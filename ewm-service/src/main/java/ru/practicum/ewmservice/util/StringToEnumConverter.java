package ru.practicum.ewmservice.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.ewmservice.model.CommentSort;


public class StringToEnumConverter implements Converter<String, CommentSort> {
    @Override
    public CommentSort convert(String source) {
        return CommentSort.valueOf(source.toUpperCase());
    }
}
