package ms.productService.controllers;

import ms.productService.entity.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ms.productService.services.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {


    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @GetMapping
    public List<Category> getAllCategory() {

        var categories = categoryService.getAllCategory();

        return categories;
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);

        return ResponseEntity.ok("Categoria excluida com sucesso.");
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,@RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

}

