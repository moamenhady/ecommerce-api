package dev.moamenhady.ecommerce.api.service;

import dev.moamenhady.ecommerce.api.model.domain.Product;
import dev.moamenhady.ecommerce.api.model.dto.ProductUpdateDto;
import dev.moamenhady.ecommerce.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Value("${ATTACHMENT_PATH}")
    private String attachmentPath;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product, MultipartFile image) {
        try {
            String imagePath = saveImageToFileSystem(image);
            product.setImagePath(imagePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return productRepository.save(product);
    }

    private String saveImageToFileSystem(MultipartFile image) throws IOException {

        String uploadDir = attachmentPath;
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();

        Path filePath = Paths.get(uploadDir, filename);
        Files.write(filePath, image.getBytes());

        return filePath.toString();
    }

    public Product updateProduct(Long id, ProductUpdateDto productUpdateDto) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(productUpdateDto.name());
            existingProduct.setPrice(productUpdateDto.price());
            existingProduct.setDescription(productUpdateDto.description());
            existingProduct.setActive(productUpdateDto.active());
            return productRepository.save(existingProduct);
        }
        throw new IllegalArgumentException("Product not found");
    }

    public Product deactivateProduct(Long id) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setActive(false);
            return productRepository.save(existingProduct);
        }
        throw new IllegalArgumentException("Product not found");
    }

    public List<Product> getProducts(int pageSize, int offset) {
        Pageable pageable = PageRequest.of(offset, pageSize);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();
    }

    public int getTotalCount() {
        return (int) productRepository.count();
    }
}
