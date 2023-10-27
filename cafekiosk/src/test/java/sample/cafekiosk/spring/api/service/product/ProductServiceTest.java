package sample.cafekiosk.spring.api.service.product;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductRepository;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1 증가한 값이다")
    void createProduct() {
        // given
        Product product = createProduct("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "아메리카노", 4000);
        productRepository.save(product);

        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                                                                         .type(ProductType.HANDMADE)
                                                                         .sellingStatus(ProductSellingStatus.SELLING)
                                                                         .name("카푸치노")
                                                                         .price(5000)
                                                                         .build();
        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        Assertions.assertThat(productResponse)
                  .extracting("productNumber", "type", "sellingStatus", "name", "price")
                  .contains("002", ProductType.HANDMADE, ProductSellingStatus.SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        Assertions.assertThat(products).hasSize(2)
                  .extracting("productNumber", "type", "sellingStatus", "name", "price")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "아메리카노", 4000),
                      Tuple.tuple("002", ProductType.HANDMADE, ProductSellingStatus.SELLING, "카푸치노", 5000)
                  );
    }

    @Test
    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품번호는 001이다")
    void createProductWhenProductsIsEmpty() {
        // given
        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                                                                         .type(ProductType.HANDMADE)
                                                                         .sellingStatus(ProductSellingStatus.SELLING)
                                                                         .name("카푸치노")
                                                                         .price(5000)
                                                                         .build();
        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        Assertions.assertThat(productResponse)
                  .extracting("productNumber", "type", "sellingStatus", "name", "price")
                  .contains("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        Assertions.assertThat(products).hasSize(1)
                  .extracting("productNumber", "type", "sellingStatus", "name", "price")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", ProductType.HANDMADE, ProductSellingStatus.SELLING, "카푸치노", 5000)
                  );
    }

    private Product createProduct(String productNumber, ProductType type, ProductSellingStatus status, String name, int price) {
        return Product.builder()
                      .productNumber(productNumber)
                      .type(type)
                      .sellingStatus(status)
                      .name(name)
                      .price(price)
                      .build();
    }
}