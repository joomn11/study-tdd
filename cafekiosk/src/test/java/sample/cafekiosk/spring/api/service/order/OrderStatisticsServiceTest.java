package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sample.cafekiosk.spring.IntegrationSupport;
import sample.cafekiosk.spring.api.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.api.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.api.domain.order.Order;
import sample.cafekiosk.spring.api.domain.order.OrderRepository;
import sample.cafekiosk.spring.api.domain.order.OrderStatus;
import sample.cafekiosk.spring.api.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductRepository;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;

@SpringBootTest
class OrderStatisticsServiceTest extends IntegrationSupport {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @Test
    void sendOrderStatisticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2023, 10, 27, 0, 0);

        Product product1 = createProduct(ProductType.HANDMADE, "001", 1000);
        Product product2 = createProduct(ProductType.HANDMADE, "002", 3000);
        Product product3 = createProduct(ProductType.HANDMADE, "003", 5000);

        List<Product> products = List.of(product1, product2, product3);

        Order order1 = createPaymentCompletedOrder(products, LocalDateTime.of(2023, 10, 26, 23, 59, 59));
        Order order2 = createPaymentCompletedOrder(products, now);
        Order order3 = createPaymentCompletedOrder(products, LocalDateTime.of(2023, 10, 27, 23, 59, 59));
        Order order4 = createPaymentCompletedOrder(products, LocalDateTime.of(2023, 10, 28, 0, 0));

        Mockito.when(mailSendClient.sendEmail(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
               .thenReturn(true);

        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2023, 10, 27), "test@test.com");

        // then
        Assertions.assertThat(result).isTrue();
        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        Assertions.assertThat(histories).hasSize(1).extracting("content").contains("total sum is 18000");
    }

    private Order createPaymentCompletedOrder(List<Product> products, LocalDateTime registeredDateTime) {
        Order order = Order.builder()
                           .registeredDateTime(registeredDateTime)
                           .products(products)
                           .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                           .build();
        return orderRepository.save(order);
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