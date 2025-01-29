package com.pc.ecom.Service;

import com.pc.ecom.Exceptions.APIException;
import com.pc.ecom.Exceptions.ResourceNotFoundException;
import com.pc.ecom.Model.Category;
import com.pc.ecom.Payload.CategoryDTO;
import com.pc.ecom.Payload.CategoryResponse;
import com.pc.ecom.Repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String order) throws APIException {

        Sort sortByAndOrder = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);


        List<Category> allCategories = categoryPage.getContent();
        if(allCategories.isEmpty()){
            throw new APIException("No categories found");
        }

        List<CategoryDTO> categoryDTOS = allCategories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);

        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setTotalElements(categoryPage.getNumberOfElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

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
    public CategoryDTO deleteCategory(Long id) {

        Optional<Category> currentCategoryOptional = categoryRepository.findById(id);

        if(currentCategoryOptional.isEmpty()) {
            throw new APIException(String.format("Category %s found",id));
        }
        else{
            Category currentCategory = currentCategoryOptional
                    .orElseThrow(()-> new ResourceNotFoundException("Category",id,"CategoryId"));

            categoryRepository.delete(currentCategory);
            return modelMapper.map(currentCategory, CategoryDTO.class);
        }
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long id) {
        Category category = modelMapper.map( categoryDTO, Category.class);

        Optional<Category> currentCategoryOptional = categoryRepository.findById(id);

        Category currentCategory = currentCategoryOptional
                .orElseThrow(()-> new ResourceNotFoundException("Category",id,"CategoryId"));


        category.setCategoryId(currentCategory.getCategoryId());
        Category updatedCategory = categoryRepository.save(category);

        return modelMapper.map(updatedCategory, CategoryDTO.class);    }
}
