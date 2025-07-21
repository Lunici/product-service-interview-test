package test.amssoluctions.producteservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import test.amssoluctions.producteservice.client.ProductApiClient;
import test.amssoluctions.producteservice.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductApiClient productApiClient;

    @Autowired
    public ProductService(ProductApiClient productApiClient) {
        this.productApiClient = productApiClient;
    }

    public List<Product> getSimilarProducts(final int productId) {
        List<Integer> similarProductsIds;
        try {
            similarProductsIds = productApiClient.getSimilarProductsIds(productId);
        } catch (RestClientException e) {
            LOGGER.error("Failed to get similar products by id {}", productId);
            similarProductsIds = Collections.emptyList();
        }

        if (similarProductsIds.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Product> similarProducts = new ArrayList<>();

        final int size = similarProductsIds.size();
        for (int i = 0; i < size; i++) {
            try {
                final Product product = productApiClient.getProductById(similarProductsIds.get(i));
                if (product != null) {
                    similarProducts.add(product);
                }
            }
            catch (HttpServerErrorException.InternalServerError e) {
                LOGGER.error("Failed to get product by id {}", similarProductsIds.get(i));
            }
        }

        return similarProducts;
    }

}
