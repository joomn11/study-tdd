package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductRepository;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문번호 리스트를 받아 주문을 생성한다")
    void createOrder() {
        // given
        Product product1 = createProduct(ProductType.HANDMADE, "001", 1000);
        Product product2 = createProduct(ProductType.HANDMADE, "002", 3000);
        Product product3 = createProduct(ProductType.HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                                                       .productNumbers(List.of("001", "002"))
                                                       .build();
        // when
        OrderResponse orderResponse = orderService.createOrder(request, LocalDateTime.now());

        // then
        Assertions.assertThat(orderResponse.getId()).isNotNull();
        Assertions.assertThat(orderResponse.getProducts()).hasSize(2)
                  .extracting("productNumber", "price")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", 1000),
                      Tuple.tuple("002", 3000)
                  );
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                      .type(type)
                      .productNumber(productNumber)
                      .price(price)
                      .sellingStatus(ProductSellingStatus.SELLING)
                      .name("menuName")
                      .build();
    }
}