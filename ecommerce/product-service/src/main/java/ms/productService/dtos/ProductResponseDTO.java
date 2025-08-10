package ms.productService.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDTO(
        String name,
        String description,
        BigDecimal price,
        StatusProduct status,
        LocalDateTime createdIn,
        LocalDateTime updatedIn,
        String categoryName
) {}
