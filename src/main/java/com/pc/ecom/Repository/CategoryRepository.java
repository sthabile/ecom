package com.pc.ecom.Repository;

import com.pc.ecom.Model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Notes:
 * CrudRepository vs JpaRepository
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(@NotBlank() @Size(min = 5, message = "Category Name must be at least 5 characters") String categoryName);
}
