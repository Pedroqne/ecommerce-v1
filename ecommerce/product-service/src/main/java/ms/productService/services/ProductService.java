package ms.productService.services;

import ms.productService.dtos.PaginatedResponse;
import ms.productService.dtos.ProductRequestDTO;
import ms.productService.dtos.ProductResponseDTO;
import ms.productService.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ms.productService.repositories.CategoryRepository;
import ms.productService.repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponseEntity<Void> createProduct(ProductRequestDTO dto) {

        var product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStatus(dto.status());
        product.setCreatedIn(LocalDateTime.now());

        var category = categoryRepository.findByName(dto.categoryName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        product.setCategory(category);
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ProductResponseDTO getProductById(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        return new ProductResponseDTO(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                product.getCreatedIn(),
                product.getUpdatedIn(),
                product.getCategory().getName()
        );
    }

    public List<ProductResponseDTO> findAllProducts() {

        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum produto encontrado.");
        }

        return products.stream()
                .map(p -> new ProductResponseDTO(
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStatus(),
                        p.getUpdatedIn(),
                        p.getCreatedIn(),
                        p.getCategory().toString()
                )).toList();
    }

    public List<ProductResponseDTO> findAllProductsByCategory(String categoryName) {

        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum produto encontrado.");
        }

        return products
                .stream()
                .filter(p -> p.getCategory().getName().equals(categoryName))
                .map(p -> new ProductResponseDTO(
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStatus(),
                        p.getUpdatedIn(),
                        p.getCreatedIn(),
                        p.getCategory().toString()
                )).collect(Collectors.toList());
    }

    public PaginatedResponse<ProductResponseDTO> getPaginationProducts(int page, int size) {

        Page<Product> pagedResult = productRepository.findAll(PageRequest.of(page, size, Sort.by("name").descending()));

        List<ProductResponseDTO> products = pagedResult.getContent()
                .stream()
                .map(p -> new ProductResponseDTO(
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStatus(),
                p.getUpdatedIn(),
                p.getCreatedIn(),
                p.getCategory().toString()
        )).collect(Collectors.toList());



        return new PaginatedResponse<> (
                products,
                pagedResult.getNumber(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages()
        );

    }

    public ResponseEntity<Void> deleteProduct(Long id){

        var product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

        productRepository.deleteById(id);

        return ResponseEntity.noContent().build();

    }

    public ResponseEntity<Optional<Product>> updateProduct(Long id, ProductRequestDTO dto) {

        var product = productRepository.findById((id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
                );

        var category = categoryRepository.findByName(dto.categoryName());

        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setStatus(dto.status());
        product.setCategory(category.get());
        product.setDescription(dto.description());
        product.setUpdatedIn(LocalDateTime.now());


        Optional<Product> productAtualizado = productRepository.findById((id));

        return ResponseEntity.ok(productAtualizado);
    }



}
