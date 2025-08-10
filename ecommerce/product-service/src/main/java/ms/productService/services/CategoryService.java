package ms.productService.services;

import ms.productService.entity.Category;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ms.productService.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }



    public Category createCategory(Category catergory) {

        categoryRepository.save(catergory);

        return ResponseEntity.status(HttpStatus.CREATED).body(catergory).getBody();
    }

    public List<Category> getAllCategory() {
        var categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma categoria foi encontrada.");
        }

        return categories;
    }

    public Category getCategoryById(Long id) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma categoria com id correspondente."));

        return category;
    }

    public ResponseEntity<Void> deleteCategory(Long id) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        categoryRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    public Category updateCategory(Long id, Category categoryUpdate){

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        category.setName(categoryUpdate.getName());
        category.setDescription(categoryUpdate.getDescription());
        category.setUpdateIn(LocalDateTime.now());
        category.setProducts(categoryUpdate.getProducts());

        return category;

    }

}


