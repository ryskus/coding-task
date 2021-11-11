package com.codingtask.model.form;

import com.codingtask.model.form.validation.ValidProductId;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class OrderItemForm {
    @ValidProductId
    private Long productId;
    @Min(1)
    private Integer amount;
}
