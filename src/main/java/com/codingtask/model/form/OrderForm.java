package com.codingtask.model.form;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class OrderForm {
    @Email
    private String buyersEmail;
    @NotEmpty
    private List<@Valid OrderItemForm> items;
}
