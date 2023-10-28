package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.domain.order.Order;
import sample.cafekiosk.spring.api.domain.order.OrderRepository;
import sample.cafekiosk.spring.api.domain.order.OrderStatus;
import sample.cafekiosk.spring.api.service.mail.MailService;

@RequiredArgsConstructor
@Service
public class OrderStatisticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticsMail(LocalDate orderDate, String mail) {
        // get pay completed order on orderDate
        List<Order> orders = orderRepository.findOrdersBy(
            orderDate.atStartOfDay(),
            orderDate.plusDays(1).atStartOfDay(),
            OrderStatus.PAYMENT_COMPLETED
        );

        // sum all price
        int totalAmount = orders.stream().mapToInt(Order::getTotalPrice).sum();

        // send mail
        boolean result = mailService.sendMail(
            "no-reply@cafekiosk.com",
            mail,
            String.format("[TotalSum] %s", orderDate),
            String.format("total sum is %s", totalAmount)
        );

        if (!result) {
            throw new IllegalArgumentException("Failed to send email of total sum ");
        }

        return true;
    }
}
