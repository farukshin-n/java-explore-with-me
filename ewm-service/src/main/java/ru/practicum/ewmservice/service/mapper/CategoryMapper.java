package ru.practicum.ewmservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.CategoryDto;
import ru.practicum.ewmservice.dto.NewCategoryDto;
import ru.practicum.ewmservice.model.Category;

@Component
public class CategoryMapper {
    public static Category toCategory(NewCategoryDto categoryDto) {
        return new Category(
                null,
                categoryDto.getName()
        );
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
