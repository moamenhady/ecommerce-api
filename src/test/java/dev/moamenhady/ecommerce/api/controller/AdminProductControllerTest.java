package dev.moamenhady.ecommerce.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import dev.moamenhady.ecommerce.api.model.domain.Product;
import dev.moamenhady.ecommerce.api.model.dto.ProductUpdateDto;
import dev.moamenhady.ecommerce.api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class AdminProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminProductController adminProductController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminProductController).build();
    }

    @Test
    void testAddProduct() throws Exception {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);

        MockMultipartFile image = new MockMultipartFile(
                "image", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when((productService).saveProduct(any(Product.class), any(MultipartFile.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(multipart("/api/admin/add-product")
                        .file("image", image.getBytes())
                        .param("product", "{\"name\":\"Test Product\",\"price\":100.0}") // Simulate JSON payload
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product added successfully"));

        verify(productService, times(1)).saveProduct(any(Product.class), any(MultipartFile.class));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Arrange
        long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Name");
        updatedProduct.setPrice(150.0);
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setActive(true);

        when((productService).updateProduct(eq(productId), any(ProductUpdateDto.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/api/admin/update-product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"price\":150.0,\"description\":\"Updated Description\",\"active\":true}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully"));

        verify(productService, times(1)).updateProduct(eq(productId), any(ProductUpdateDto.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Arrange
        long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setActive(true);

        when((productService).deactivateProduct(productId)).thenReturn(existingProduct);

        // Act & Assert
        mockMvc.perform(post("/api/admin/delete-product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));

        verify(productService, times(1)).deactivateProduct(productId);
    }

    @Test
    void testListProducts() throws Exception {
        // Arrange
        int pageSize = 10;
        int offset = 0;
        List<Product> products = Collections.singletonList(new Product());
        int totalCount = 1;

        when(productService.getProducts(pageSize, offset)).thenReturn(products);
        when(productService.getTotalCount()).thenReturn(totalCount);

        // Act & Assert
        mockMvc.perform(get("/api/admin/list-products")
                        .param("pageSize", String.valueOf(pageSize))
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk())
                .andExpect(header().string(String.valueOf(totalCount), String.valueOf(totalCount)))
                .andExpect(jsonPath("$[0]").exists());

        verify(productService, times(1)).getProducts(pageSize, offset);
        verify(productService, times(1)).getTotalCount();
    }
}