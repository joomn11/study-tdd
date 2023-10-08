package sample.cafekiosk.spring.api.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;

class OrderTest {

    @Test
    @DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다")
    void calculateTotalPrice() {
        List<Product> products = List.of(
            createProduct("001", 1000),
            createProduct("002", 2000)
        );

        Order order = Order.create(products, LocalDateTime.now());

        Assertions.assertThat(order.getTotalPrice()).isEqualTo(3000);
    }

    @Test
    @DisplayName("주문 생성 시 주문 상태는 INIT이다")
    void initOrder() {
        List<Product> products = List.of(
            createProduct("001", 1000),
            createProduct("002", 2000)
        );

        Order order = Order.create(products, LocalDateTime.now());

        Assertions.assertThat(order.getOrderStatus()).isEqualByComparingTo(OrderStatus.INIT);
    }

    @Test
    @DisplayName("주문 생성 시 주문 등록 시간을 기록한다")
    void registeredDateTime() {
        List<Product> products = List.of(
            createProduct("001", 1000),
            createProduct("002", 2000)
        );
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Order order = Order.create(products, registeredDateTime);

        Assertions.assertThat(order.getRegisteredDateTime()).isEqualTo(registeredDateTime);
    }


    private Product createProduct(String productNumber, int price) {
        return Product.builder()
                      .type(ProductType.HANDMADE)
                      .productNumber(productNumber)
                      .price(price)
                      .sellingStatus(ProductSellingStatus.SELLING)
                      .name("menuName")
                      .build();
    }
}