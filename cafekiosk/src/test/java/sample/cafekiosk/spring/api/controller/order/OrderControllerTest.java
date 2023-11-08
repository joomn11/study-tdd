package sample.cafekiosk.spring.api.controller.order;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sample.cafekiosk.spring.ControllerTestSupport;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;


class OrderControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("신규 주문을 등록한다")
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                                                       .productNumbers(List.of("001"))
                                                       .build();
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/new")
                                              .content(objectMapper.writeValueAsString(request))
                                              .contentType(MediaType.APPLICATION_JSON)
               )
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("신규 주문을 등록할 때 상품번호는 1개 이상이어야 한다")
    void createOrderWithEmptyProductNumber() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                                                       .productNumbers(List.of())
                                                       .build();
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders/new")
                                              .content(objectMapper.writeValueAsString(request))
                                              .contentType(MediaType.APPLICATION_JSON)
               )
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 번호 리스트는 필수"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }
}