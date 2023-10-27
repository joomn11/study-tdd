package sample.cafekiosk.spring.api.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("신규 상품을 등록한다")
    void createProduct() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                                                           .type(ProductType.HANDMADE)
                                                           .sellingStatus(ProductSellingStatus.SELLING)
                                                           .name("아메리카노")
                                                           .price(4000)
                                                           .build();
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                                              .content(objectMapper.writeValueAsString(request))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("신규 상품을 등록할 때 상품 타입은 필수")
    void createProductWithoutType() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                                                           .sellingStatus(ProductSellingStatus.SELLING)
                                                           .name("아메리카노")
                                                           .price(4000)
                                                           .build();
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")
                                              .content(objectMapper.writeValueAsString(request))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 타입은 필수"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("판매 상품을 조회한다")
    void getSellingProducts() throws Exception {
        // given
        List<ProductResponse> result = List.of();
        BDDMockito.when(productService.getSellingProducts()).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/selling"))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.name()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(HttpStatus.OK.name()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());

    }
}