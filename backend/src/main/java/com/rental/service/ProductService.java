package com.rental.service;

import com.rental.entity.Product;
import com.rental.enums.ProductStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {
    private final Map<Long, Product> productRepository = new ConcurrentHashMap<>();
    private final AtomicLong productIdGenerator = new AtomicLong(1);

    @PostConstruct
    public void initProducts() {
        Product p1 = new Product();
        p1.setName("单反相机");
        p1.setDescription("专业级单反相机，适合摄影爱好者");
        p1.setDailyRate(new BigDecimal("50.00"));
        p1.setDeposit(new BigDecimal("2000.00"));
        p1.setStatus(ProductStatus.IDLE);
        createProduct(p1);

        Product p2 = new Product();
        p2.setName("笔记本电脑");
        p2.setDescription("高性能商务笔记本");
        p2.setDailyRate(new BigDecimal("80.00"));
        p2.setDeposit(new BigDecimal("3000.00"));
        p2.setStatus(ProductStatus.IDLE);
        createProduct(p2);

        Product p3 = new Product();
        p3.setName("电动自行车");
        p3.setDescription("城市通勤电动自行车");
        p3.setDailyRate(new BigDecimal("30.00"));
        p3.setDeposit(new BigDecimal("1000.00"));
        p3.setStatus(ProductStatus.IDLE);
        createProduct(p3);

        Product p4 = new Product();
        p4.setName("无人机");
        p4.setDescription("航拍无人机，4K摄像头");
        p4.setDailyRate(new BigDecimal("100.00"));
        p4.setDeposit(new BigDecimal("5000.00"));
        p4.setStatus(ProductStatus.IDLE);
        createProduct(p4);

        Product p5 = new Product();
        p5.setName("投影仪");
        p5.setDescription("高清家用投影仪");
        p5.setDailyRate(new BigDecimal("40.00"));
        p5.setDeposit(new BigDecimal("1500.00"));
        p5.setStatus(ProductStatus.IN_REPAIR);
        createProduct(p5);
    }

    public Product createProduct(Product product) {
        Long id = productIdGenerator.getAndIncrement();
        product.setId(id);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.put(id, product);
        return product;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productRepository.values());
    }

    public Product getProductById(Long id) {
        return productRepository.get(id);
    }

    public Product updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.get(id);
        if (product != null) {
            product.setStatus(status);
            product.setUpdatedAt(LocalDateTime.now());
        }
        return product;
    }

    public boolean isProductAvailable(Long id) {
        Product product = productRepository.get(id);
        return product != null && product.getStatus() == ProductStatus.IDLE;
    }
}
