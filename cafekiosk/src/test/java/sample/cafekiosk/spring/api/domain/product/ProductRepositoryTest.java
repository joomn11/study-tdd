package sample.cafekiosk.spring.api.domain.product;

import static sample.cafekiosk.spring.api.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.spring.api.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.api.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.spring.api.domain.product.ProductType.HANDMADE;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다")
    void findAllBySellingStateIn() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

        // then
        Assertions.assertThat(products).hasSize(2)
                  .extracting("productNumber", "name", "sellingStatus")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", "아메리카노", SELLING),
                      Tuple.tuple("002", "카페라떼", HOLD)
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

    @Test
    @DisplayName("상품번호 리스트로 상품들을 조회한다")
    void findAllByProductNumberIn() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllByProductNumberIn(List.of("001", "002"));

        // then
        Assertions.assertThat(products).hasSize(2)
                  .extracting("productNumber", "name", "sellingStatus")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", "아메리카노", SELLING),
                      Tuple.tuple("002", "카페라떼", HOLD)
                  );
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다")
    void findLatestProductNumber() {
        // given
        Product product = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        productRepository.save(product);

        // when
        String latestProductNumber = productRepository.findLatestProductNumber();

        // then
        Assertions.assertThat(latestProductNumber).isEqualTo("001");
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올 때, 상품이 하나도 없는 경우에는 null을 반환한다")
    void findLatestProductNumberWhenProductIsEmpty() {
        // given

        // when
        String latestProductNumber = productRepository.findLatestProductNumber();

        // then
        Assertions.assertThat(latestProductNumber).isNull();
    }

}