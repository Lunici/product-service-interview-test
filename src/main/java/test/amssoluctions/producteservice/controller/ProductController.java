package test.amssoluctions.producteservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import test.amssoluctions.producteservice.model.Product;
import test.amssoluctions.producteservice.service.ProductService;

import java.util.List;

@RestController("/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(@PathVariable Integer productId) {
        LOGGER.info("Received request GET /product/{}/similar", productId);
        return ResponseEntity.ok(productService.getSimilarProducts(productId));
    }

}
