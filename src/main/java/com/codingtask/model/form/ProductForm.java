package com.codingtask.model.form;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductForm {
    @NotBlank
    private String name;

    @NotNull
    @DecimalMin(value = "0.1")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal price;
}
