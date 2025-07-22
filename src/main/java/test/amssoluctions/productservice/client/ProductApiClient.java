package test.amssoluctions.productservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import test.amssoluctions.productservice.model.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class ProductApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductApiClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public ProductApiClient(RestTemplate restTemplate, @Value("${api-client.product-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @TimeLimiter(name = "productApiClientTimeLimiter")
    @CircuitBreaker(name = "productApiClientCircuitBreaker")
    public CompletableFuture<List<Integer>> getSimilarProductsIds(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final ResponseEntity<List<Integer>> response = restTemplate.exchange(baseUrl + "/{productId}/similarids", HttpMethod.GET, null, new ParameterizedTypeReference<List<Integer>>() {}, productId);
                return response.getBody();
            }
            catch (RestClientException e) {
                LOGGER.error("GET:http://localhost:3001/product/{}/similarids -> HTTP error:{}", productId, e.getMessage());
                throw e;
            }
        });
    }

    @TimeLimiter(name = "productApiClientTimeLimiter")
    @CircuitBreaker(name = "productApiClientCircuitBreaker")
    public CompletableFuture<Product> getProductById(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final ResponseEntity<Product> response = restTemplate.getForEntity(baseUrl + "/{productId}", Product.class, productId);
                return response.getBody();
            } catch (HttpClientErrorException.NotFound e) {
                return null;
            } catch (HttpServerErrorException.InternalServerError e) {
                LOGGER.error("GET:http://localhost:3001/product/{} -> Internal server error:500", productId);
                throw e;
            }
        });
    }

}
