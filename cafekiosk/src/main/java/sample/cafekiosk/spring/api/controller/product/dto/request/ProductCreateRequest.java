package sample.cafekiosk.spring.api.controller.product.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.api.domain.product.Product;
import sample.cafekiosk.spring.api.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.api.domain.product.ProductType;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull(message = "상품 타입은 필수")
    private ProductType type;
    @NotNull(message = "상품 판매상태는 필수")
    private ProductSellingStatus sellingStatus;
    @NotBlank(message = "상품 이름은 필수")
    private String name;
    @Positive(message = "상품 가격은 양수")
    private int price;

    @Builder
    private ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }

    public Product toEntity(String nextProductNumber) {
        return Product.builder()
                      .productNumber(nextProductNumber)
                      .type(type)
                      .sellingStatus(sellingStatus)
                      .name(name)
                      .price(price)
                      .build();
    }

    public ProductCreateServiceRequest toServiceRequest() {
        return ProductCreateServiceRequest.builder()
                                          .type(type)
                                          .sellingStatus(sellingStatus)
                                          .name(name)
                                          .price(price)
                                          .build();
    }
}
