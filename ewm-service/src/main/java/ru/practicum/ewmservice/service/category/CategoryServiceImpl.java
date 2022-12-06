package ru.practicum.ewmservice.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.category.CategoryDto;
import ru.practicum.ewmservice.dto.category.NewCategoryDto;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.service.mapper.CategoryMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto add(NewCategoryDto newCategory) {
        Category categoryToSave = CategoryMapper.toCategory(newCategory);
        Category savedCategory = categoryRepository.save(categoryToSave);
        log.info("New category with id={} added successfully.", savedCategory.getId());
        return CategoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto get(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There isn't category with id=%d in repository.", categoryId)
                ));
        log.info("Getting category with id={} from repository.", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto newCategory) {
        Category category = categoryRepository.findById(newCategory.getId())
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't category with id=%d in repository.", newCategory.getId())
                ));
        if (newCategory.getName() != null) {
            category.setName(newCategory.getName());
        }
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with id={} updated successfully.", updatedCategory.getId());
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        log.info("Category with if={} deleted successfully.", categoryId);
    }
}
