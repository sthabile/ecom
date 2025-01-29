package com.pc.ecom.Service;

import com.pc.ecom.Exceptions.APIException;
import com.pc.ecom.Exceptions.ResourceNotFoundException;
import com.pc.ecom.Model.Category;
import com.pc.ecom.Payload.CategoryDTO;
import com.pc.ecom.Payload.CategoryResponse;
import com.pc.ecom.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<Category>();
    private Long nextId = 1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
//        return categories;
        List<Category> allCategories = categoryRepository.findAll();
        if(allCategories.isEmpty()){
            throw new APIException("No categories found");
        }

        List<CategoryDTO> categoryDTOS = allCategories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {

        Category category = modelMapper.map(categoryDto, Category.class);

        category.setCategoryId(nextId++);

        Optional<Category> categoryOptional = categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryOptional.isPresent()) {
            throw new APIException(String.format("Category %s already exists", category.getCategoryName()));
        }

        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public String deleteCategory(Long id) {

        Optional<Category> currentCategoryOptional = categoryRepository.findById(id);

        if(currentCategoryOptional.isEmpty()) {
            throw new APIException(String.format("Category %s found",id));
        }
        else{
            Category currentCategory = currentCategoryOptional
                    .orElseThrow(()-> new ResourceNotFoundException("Category",id,"CategoryId"));

            categoryRepository.delete(currentCategory);
            return "Category with id : " + id + "successfully deleted";
        }
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        Optional<Category> currentCategoryOptional = categoryRepository.findById(id);

        Category currentCategory = currentCategoryOptional
                .orElseThrow(()-> new ResourceNotFoundException("Category",id,"CategoryId"));


        category.setCategoryId(currentCategory.getCategoryId());
        currentCategory = categoryRepository.save(category);
        return currentCategory;
    }
}
