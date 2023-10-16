package sample.cafekiosk.spring.api.domain.stock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockTest {

    @Test
    @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다")
    void isQuantityLessThan() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        // when
        boolean result = stock.isQuantityLessThan(quantity);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("재고를 주어진 갯수만큼 차감할 수 있다")
    void deduceQuantity() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 1;

        // when
        stock.deduceQuantity(quantity);

        // then
        Assertions.assertThat(stock.getQuantity()).isZero();
    }

    @Test
    @DisplayName("재고보다 많은 수의 수량으로 차감 시도하는 경우 예외가 발생한다")
    void deduceQuantity2() {
        // given
        Stock stock = Stock.create("001", 1);
        int quantity = 2;

        // when then
        Assertions.assertThatThrownBy(() -> stock.deduceQuantity(quantity))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessage("차감할 재고 수량이 없습니다");
    }
}