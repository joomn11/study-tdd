package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.domain.order.Order;
import sample.cafekiosk.spring.api.domain.order.OrderRepository;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductRepository;
import sample.cafekiosk.spring.api.domain.product.ProductType;
import sample.cafekiosk.spring.api.domain.stock.Stock;
import sample.cafekiosk.spring.api.domain.stock.StockRepository;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductBy(productNumbers);

        deductStockQuantities(products);

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.of(savedOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        List<String> stockProductNumbers = extractStockProductNumbers(products);

        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if (stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다");
            }
            stock.deduceQuantity(quantity);
        }
    }

    private Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                                  .collect(Collectors.groupingBy(productNumber -> productNumber, Collectors.counting()));
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                     .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }

    private List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                       .filter(p -> ProductType.containsStockType(p.getType()))
                       .map(Product::getProductNumber)
                       .collect(Collectors.toList());
    }

    private List<Product> findProductBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                                                  .collect(Collectors.toMap(Product::getProductNumber, p -> p));

        return productNumbers.stream()
                             .map(productMap::get)
                             .collect(Collectors.toList());
    }
}
