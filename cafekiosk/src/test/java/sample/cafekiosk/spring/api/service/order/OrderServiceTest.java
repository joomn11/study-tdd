package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.api.domain.order.OrderRepository;
import sample.cafekiosk.spring.api.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductRepository;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;
import sample.cafekiosk.spring.api.domain.stock.Stock;
import sample.cafekiosk.spring.api.domain.stock.StockRepository;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private OrderService orderService;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        orderProductRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("주문번호 리스트를 받아 주문을 생성한다")
    void createOrder() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(ProductType.HANDMADE, "001", 1000);
        Product product2 = createProduct(ProductType.HANDMADE, "002", 3000);
        Product product3 = createProduct(ProductType.HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                                                                     .productNumbers(List.of("001", "002"))
                                                                     .build();
        // when
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        // then
        Assertions.assertThat(orderResponse.getId()).isNotNull();
        Assertions.assertThat(orderResponse)
                  .extracting("registeredDateTime", "totalPrice")
                  .contains(registeredDateTime, 4000);
        Assertions.assertThat(orderResponse.getProducts()).hasSize(2)
                  .extracting("productNumber", "price")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", 1000),
                      Tuple.tuple("002", 3000)
                  );
    }

    @Test
    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다")
    void createDuplicatedOrder() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(ProductType.HANDMADE, "001", 1000);
        Product product2 = createProduct(ProductType.HANDMADE, "002", 3000);
        Product product3 = createProduct(ProductType.HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                                                                     .productNumbers(List.of("001", "001"))
                                                                     .build();
        // when
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        // then
        Assertions.assertThat(orderResponse.getId()).isNotNull();
        Assertions.assertThat(orderResponse)
                  .extracting("registeredDateTime", "totalPrice")
                  .contains(registeredDateTime, 2000);
        Assertions.assertThat(orderResponse.getProducts()).hasSize(2)
                  .extracting("productNumber", "price")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", 1000),
                      Tuple.tuple("001", 1000)
                  );
    }

    @Test
    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다")
    void createOrderWithStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(ProductType.BOTTLE, "001", 1000);
        Product product2 = createProduct(ProductType.BAKERY, "002", 3000);
        Product product3 = createProduct(ProductType.HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                                                                     .productNumbers(List.of("001", "002", "001", "003"))
                                                                     .build();
        // when
        OrderResponse orderResponse = orderService.createOrder(request, registeredDateTime);

        // then
        Assertions.assertThat(orderResponse.getId()).isNotNull();
        Assertions.assertThat(orderResponse)
                  .extracting("registeredDateTime", "totalPrice")
                  .contains(registeredDateTime, 10000);
        Assertions.assertThat(orderResponse.getProducts()).hasSize(4)
                  .extracting("productNumber", "price")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", 1000),
                      Tuple.tuple("001", 1000),
                      Tuple.tuple("003", 5000),
                      Tuple.tuple("002", 3000)
                  );

        List<Stock> stocks = stockRepository.findAll();
        Assertions.assertThat(stocks).hasSize(2)
                  .extracting("productNumber", "quantity")
                  .containsExactlyInAnyOrder(
                      Tuple.tuple("001", 0),
                      Tuple.tuple("002", 1)
                  );
    }

    @Test
    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다")
    void createOrderWithNoStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(ProductType.BOTTLE, "001", 1000);
        Product product2 = createProduct(ProductType.BAKERY, "002", 3000);
        Product product3 = createProduct(ProductType.HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stock1.deduceQuantity(1); //TODO
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                                                                     .productNumbers(List.of("001", "002", "001", "003"))
                                                                     .build();
        // when then
        Assertions.assertThatThrownBy(() -> orderService.createOrder(request, registeredDateTime))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessage("재고가 부족한 상품이 있습니다");
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