package ms.productService.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(max = 150)
        String name,

        @Size(max = 225)
        String description,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(value = "0.0", message = "O preço deve ser maior que zero.", inclusive = false)
        BigDecimal price,

        @NotNull(message = "O status é obrigatório.")
        StatusProduct status,

        @NotNull(message = "A categoria é obrigatória.")
        String categoryName
) {}