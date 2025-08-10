package ms.productService.services;

import ms.productService.dtos.ProductRequestDTO;
import ms.productService.dtos.ProductResponseDTO;
import ms.productService.dtos.StatusProduct;
import ms.productService.entity.Category;
import ms.productService.entity.Product;
import ms.productService.repositories.CategoryRepository;
import ms.productService.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductResponseDTO productResponseDT1;
    private ProductResponseDTO productResponseDT2;
    private Category category;


    private Product criarProduto(Long id, String nome, BigDecimal preco, Category categoria) {
        Product produto = new Product();
        produto.setId(id);
        produto.setName(nome);
        produto.setDescription("Descrição do " + nome);
        produto.setPrice(preco);
        produto.setStatus(StatusProduct.ATIVO);
        produto.setCreatedIn(LocalDateTime.now());
        produto.setUpdatedIn(LocalDateTime.now());
        produto.setCategory(categoria);
        return produto;
    }

    private Category criarCategoria(Long id, String nome) {
        Category categoria = new Category();
        categoria.setId(id);
        categoria.setName(nome);
        categoria.setDescription("Descrição da " + nome);
        categoria.setCreatedIn(LocalDateTime.now());
        categoria.setUpdateIn(LocalDateTime.now());
        return categoria;
    }

    @BeforeEach
    void setUp() {

        category = criarCategoria(1L, "Categoria 1");

        ;

        product1 = criarProduto(1L, "Produto 1", BigDecimal.valueOf(100), category);
        product2 = criarProduto(2L, "Produto 2", BigDecimal.valueOf(100), category);


        productResponseDT1 = new ProductResponseDTO(
                product1.getName(),
                product1.getDescription(),
                product1.getPrice(),
                product1.getStatus(),
                product1.getCreatedIn(),
                product1.getUpdatedIn(),
                product1.getCategory().getName()
        );

        productResponseDT2 = new ProductResponseDTO(
                product2.getName(),
                product2.getDescription(),
                product2.getPrice(),
                product2.getStatus(),
                product2.getCreatedIn(),
                product2.getUpdatedIn(),
                product2.getCategory().getName()
        );
    }

    @Test
    @DisplayName("Deve criar produto com sucesso quando a categoria existir")
    void deveCriarProdutoComSucesso() {

        //Arenge

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "Novo Produto",
                "Descrição do novo produto",
                new BigDecimal("100.00"),
                StatusProduct.ATIVO,
                "Eletronicos"
        );

        //Act

        when(categoryRepository.findByName("Eletronicos")).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        ResponseEntity<Void> result = productService.createProduct(productRequestDTO);


        // Assert

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryRepository, times(1)).findByName("Eletronicos");
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException (404) ao criar produto com categoria inexistente")
    void deveLancarResponseStatusExceptionQuandoCriarProdutoComCategoriaInexistente() {

        // Averege

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "Novo Produto",
                "Descrição de novo produto",
                new BigDecimal("100.00"),
                StatusProduct.ATIVO,
                "Categoria Inexistente"
        );

        // Act


        when(categoryRepository.findByName("Categoria Inexistente")).thenReturn(Optional.empty());


        // Assert


        assertThatThrownBy(() -> productService.createProduct(productRequestDTO))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

        verify(categoryRepository, times(1)).findByName("Categoria Inexistente");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve retornar todos os produtos")
    void deveRetornarTodosOsProdutos() {

        //Arange


        //Act

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<ProductResponseDTO> result = productService.findAllProducts();


        // Assert

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertEquals("Produto 1", result.get(0).name());
        assertEquals("Produto 2", result.get(1).name());


        verify(productRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("Deve retornar ProductResponseDTO quando produto é encontrado por ID")
    void deveRetornarProdutoEncontradoPeloId() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        ProductResponseDTO responseDTO = productService.getProductById(1L);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.name()).isEqualTo(product1.getName());
        assertThat(responseDTO.categoryName()).isEqualTo(product1.getCategory().getName());


        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException (404) quando produto não é encontrado por ID")
    void deveLancarResponseStatusExceptionQuandoProdutoNaoEncontradoPeloId() {

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException (404) quando nenhum produto é encontrado")
    void deveLancarResponseStatusExceptionQuandoNenhumProdutoEncontrado() {

        when(productRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> productService.findAllProducts())
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining("Nenhum produto encontrado.");
    }

    @Test
    @DisplayName("Deve retornar produtos filtrando pela categoria caso for encontrado")
    void deveRetonarProdutosFiltrandoPelaCategoria() {

        Category category2 = criarCategoria(2L, "Livros");
        Product product3 = criarProduto(1L, "Livro de Teste", new BigDecimal("100.00"), category2);

        List<Product> products = List.of(product1, product2, product3);

        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponseDTO> produtosFiltrados = productService.findAllProductsByCategory(product1.getCategory().getName());


        assertThat(produtosFiltrados).isNotNull();
        assertThat(produtosFiltrados.size()).isEqualTo(2);
        assertThat(produtosFiltrados.get(0).name()).isEqualTo(product1.getName());
        assertThat(produtosFiltrados.get(1).name()).isEqualTo(product2.getName());

        verify(productRepository, times(1)).findAll();





    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException (404) quando nenhum produto é encontrado quando filtrado pela categoria")
    void deveLancarResponseStatusExceptionQuandoProdutoNaoForEncontradoFiltradoPeloCategoria() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> productService.findAllProductsByCategory(product1.getCategory().getName()))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining("Nenhum produto encontrado.");
    }

    @Test
    @DisplayName("Deve deletar um produto com sucesso e retornar 204 NO_CONTENT")
    void deveDeletarUmProdutoComSucesso() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        doNothing().when(productRepository).deleteById(1L);

        ResponseEntity<Void> responseEntity = productService.deleteProduct(1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException (404) quando nenhum produto for encontrado para ser deletado")
    void deveLancarResponseStatusExceptionQuandoNenhumProdutoForDeletado() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessageContaining("Produto não encontrado.");


        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).deleteById(1L);
    }


}

