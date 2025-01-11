package dev.moamenhady.ecommerce.api.controller;

import dev.moamenhady.ecommerce.api.model.domain.Product;
import dev.moamenhady.ecommerce.api.model.dto.ProductUpdateDto;
import dev.moamenhady.ecommerce.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/add-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addProduct(
            @RequestPart("product") @Valid Product product,
            @RequestPart("image") MultipartFile image) {
        productService.saveProduct(product, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable long id, @RequestBody ProductUpdateDto productUpdateDto) {
        productService.updateProduct(id, productUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated successfully");
    }

    @PostMapping("/delete-product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }

    @GetMapping("/list-products")
    public ResponseEntity<List<Product>> listProducts(@RequestParam int pageSize, @RequestParam int offset) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts(pageSize, offset));
    }
}
