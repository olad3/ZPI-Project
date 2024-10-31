package com.zpi.amoz.services;

import com.zpi.amoz.models.ProductVariant;
import com.zpi.amoz.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public ProductVariantService(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    public List<ProductVariant> findAll() {
        return productVariantRepository.findAll();
    }

    public Optional<ProductVariant> findById(UUID id) {
        return productVariantRepository.findById(id);
    }

    public ProductVariant save(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    public void deleteById(UUID id) {
        productVariantRepository.deleteById(id);
    }
}
