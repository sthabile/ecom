package com.pc.ecom.Controller;

import com.pc.ecom.Model.Category;
import com.pc.ecom.Payload.CategoryDTO;
import com.pc.ecom.Payload.CategoryResponse;
import com.pc.ecom.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/")
public class CategoryController {

    //Need to inject a service class dependency
//    @Autowired
    private CategoryService categoryService;

    //Constructor injection is by default. Otherwise, could use Field injection
    // through autowire
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

//    @GetMapping("/api/public/categories")
    @RequestMapping(method = RequestMethod.GET, value = "public/categories")
    public ResponseEntity<CategoryResponse> getCategories() {
//        List<CategoryDTO> categories = categoryService.getAllCategories();
        CategoryResponse categoryResponse = categoryService.getAllCategories();
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }

//    @PostMapping("/api/public/categories")
    @RequestMapping(method = RequestMethod.POST,value = "public/categories")
    //@valid constraint/criteria can be defined in the model via annotations
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
        CategoryDTO savedCategory =  categoryService.createCategory(category); //same here. Use the injected object

//        CategoryResponse categoryResponse = new CategoryResponse();
//        categoryResponse.addToContent(savedCategory);
//        categoryResponse.setStatus(String.format("Successfully created category %s",savedCategory.getCategoryName()));
//
//        return new ResponseEntity<>(categoryResponse,HttpStatus.CREATED);\
        //I suppose we could also just return the DTO instead of the CategoryResponse
        return new ResponseEntity<>(savedCategory,HttpStatus.CREATED);
    }

//    @DeleteMapping("/api/admin/categories/{categoryId}")
    @RequestMapping(method = RequestMethod.DELETE,value = "admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO deletedCategory =  categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

//    @PutMapping("/api/public/categories/{categoryId}")
    @RequestMapping(method = RequestMethod.PUT, value = "public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO category, @PathVariable Long categoryId) {
        CategoryDTO updatedCategory = categoryService.updateCategory(category, categoryId);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }
}
