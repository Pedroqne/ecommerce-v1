package ms.productService.controllers;

import ms.productService.dtos.PaginatedResponse;
import ms.productService.dtos.ProductRequestDTO;
import ms.productService.dtos.ProductResponseDTO;
import ms.productService.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ms.productService.services.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductRequestDTO product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {

        var products = productService.findAllProducts();

        return products;
    }


    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {

        return productService.getProductById(id);
    }

    @GetMapping("/c={categoryName}")
    public List<ProductResponseDTO>  getProductByCategory(@PathVariable String categoryName) {
        return productService.findAllProductsByCategory(categoryName);
    }

    @GetMapping("/{page}/{size}")
    public PaginatedResponse<ProductResponseDTO> getPaginationProducts(@PathVariable int page, @PathVariable int size){
        return productService.getPaginationProducts(page, size);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);

        return ResponseEntity.ok("Produto excluido com sucesso.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<Product>> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO product) {
        return productService.updateProduct(id, product);
    }

}

