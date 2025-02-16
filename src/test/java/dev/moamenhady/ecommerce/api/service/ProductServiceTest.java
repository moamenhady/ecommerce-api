package dev.moamenhady.ecommerce.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import dev.moamenhady.ecommerce.api.model.domain.Product;
import dev.moamenhady.ecommerce.api.model.dto.ProductUpdateDto;
import dev.moamenhady.ecommerce.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final String attachmentPath = "test-attachments";

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
        productService.setAttachmentPath(attachmentPath); // Manually set the attachment path for testing
    }

    @Test
    void testSaveProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);

        MultipartFile image = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product savedProduct = productService.saveProduct(product, image);

        // Assert
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getImagePath());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Name");
        existingProduct.setPrice(50.0);

        ProductUpdateDto updateDto = new ProductUpdateDto("New Name", "New Description", 100.0, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product updatedProduct = productService.updateProduct(productId, updateDto);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals("New Name", updatedProduct.getName());
        assertEquals(100.0, updatedProduct.getPrice());
        assertEquals("New Description", updatedProduct.getDescription());
        assertTrue(updatedProduct.isActive());
        assertNotNull(updatedProduct.getUpdatedAt());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        // Arrange
        Long productId = 1L;
        ProductUpdateDto updateDto = new ProductUpdateDto("New Name", "New Description", 100.0, true);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(productId, updateDto));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeactivateProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setActive(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product deactivatedProduct = productService.deactivateProduct(productId);

        // Assert
        assertNotNull(deactivatedProduct);
        assertFalse(deactivatedProduct.isActive());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testDeactivateProduct_ProductNotFound() {
        // Arrange
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.deactivateProduct(productId));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetProducts() {
        // Arrange
        int pageSize = 10;
        int offset = 0;
        Pageable pageable = PageRequest.of(offset, pageSize);
        List<Product> productList = List.of(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        List<Product> products = productService.getProducts(pageSize, offset);

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetTotalCount() {
        // Arrange
        when(productRepository.count()).thenReturn(5L);

        // Act
        int totalCount = productService.getTotalCount();

        // Assert
        assertEquals(5, totalCount);
        verify(productRepository, times(1)).count();
    }

}