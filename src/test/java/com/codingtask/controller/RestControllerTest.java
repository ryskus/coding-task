package com.codingtask.controller;

import com.codingtask.Main;
import com.codingtask.model.entity.Order;
import com.codingtask.model.entity.Product;
import com.codingtask.model.form.OrderForm;
import com.codingtask.model.form.OrderItemForm;
import com.codingtask.model.form.ProductForm;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
@NoArgsConstructor
public class RestControllerTest {
    private String serviceUrl;
    @LocalServerPort
    protected int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ProductForm productForm;
    private ResponseEntity<Long> postResponse;
    private ResponseEntity<Product> getProductResponse;
    private ResponseEntity<Void> putProductResponse;
    private ResponseEntity<Order> getOrderResponse;
    private ResponseEntity<List<Order>> getOrdersResponse;

    @Before
    public void init() {
        this.serviceUrl = String.format("http://localhost:%d", port);
    }

    @Test
    public void test_happyPatch() {
        givenProduct("Test Product", new BigDecimal("10.99"))
                .whenCreatingProductWithApi()
                .then()
                .postResponseStatusIs(HttpStatus.OK)
                .and()
                .responseIdIs(1L);

        whenGettingProductWithApi(1L)
                .then()
                .getProductResponseStatusIs(HttpStatus.OK)
                .and()
                .productNameIs("Test Product");

        whenChangingProductNameWithApi(1L, "Test Product 2")
                .then()
                .putProductResponseStatusIs(HttpStatus.OK)
                .whenGettingProductWithApi(1L)
                .then()
                .productNameIs("Test Product 2");

        whenOrderingProductWithApi(1L, 10)
                .then()
                .postResponseStatusIs(HttpStatus.OK)
                .and()
                .responseIdIs(1L);

        whenGettingOrderWithApi(1L)
                .then()
                .getOrderResponseStatusIs(HttpStatus.OK)
                .and()
                .totalPriceIs(productForm.getPrice().multiply(BigDecimal.TEN));

        whenGettingOrderWithApiByDateRange(LocalDate.now().minusDays(1L), LocalDate.now())
                .then()
                .getOrdersResponseStatusIs(HttpStatus.OK)
                .and()
                .numberOfOrdersIs(1);
    }

    private RestControllerTest givenProduct(String name, BigDecimal price) {
        this.productForm = new ProductForm();
        this.productForm.setName(name);
        this.productForm.setPrice(price);
        return this;
    }

    private RestControllerTest whenCreatingProductWithApi() {
        HttpEntity<ProductForm> httpEntity = buildHttpEntity(productForm);
        this.postResponse = restTemplate.exchange(serviceUrl + "/product", HttpMethod.POST, httpEntity, Long.class);
        return this;
    }

    private RestControllerTest whenGettingProductWithApi(Long productId) {
        this.getProductResponse = restTemplate.exchange(serviceUrl + "/product/" + productId, HttpMethod.GET, null, Product.class);
        return this;
    }

    private RestControllerTest whenGettingOrderWithApi(Long orderId) {
        this.getOrderResponse = restTemplate.exchange(serviceUrl + "/order/" + orderId, HttpMethod.GET, null, Order.class);
        return this;
    }

    private RestControllerTest whenGettingOrderWithApiByDateRange(LocalDate startDate, LocalDate endDate) {
        this.getOrdersResponse = restTemplate.exchange(serviceUrl + "/order?startDate=" + startDate + "&endDate=" + endDate, HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {
        });
        return this;
    }

    private RestControllerTest whenChangingProductNameWithApi(Long productId, String newName) {
        productForm.setName(newName);
        HttpEntity<ProductForm> httpEntity = buildHttpEntity(productForm);
        this.putProductResponse = restTemplate.exchange(serviceUrl + "/product/" + productId, HttpMethod.PUT, httpEntity, Void.class);
        return this;
    }

    private RestControllerTest whenOrderingProductWithApi(Long productId, Integer amount) {
        OrderForm orderForm = new OrderForm();
        orderForm.setBuyersEmail("buyer@email.com");
        OrderItemForm orderItemForm = new OrderItemForm();
        orderItemForm.setProductId(productId);
        orderItemForm.setAmount(amount);
        orderForm.setItems(Arrays.asList(orderItemForm));
        HttpEntity<OrderForm> httpEntity = buildHttpEntity(orderForm);
        this.postResponse = restTemplate.exchange(serviceUrl + "/order", HttpMethod.POST, httpEntity, Long.class);
        return this;
    }

    private RestControllerTest postResponseStatusIs(HttpStatus expected) {
        Assert.assertEquals(expected, postResponse.getStatusCode());
        return this;
    }

    private RestControllerTest getProductResponseStatusIs(HttpStatus expected) {
        Assert.assertEquals(expected, getProductResponse.getStatusCode());
        return this;
    }

    private RestControllerTest getOrderResponseStatusIs(HttpStatus expected) {
        Assert.assertEquals(expected, getOrderResponse.getStatusCode());
        return this;
    }

    private RestControllerTest getOrdersResponseStatusIs(HttpStatus expected) {
        Assert.assertEquals(expected, getOrdersResponse.getStatusCode());
        return this;
    }

    private RestControllerTest putProductResponseStatusIs(HttpStatus expected) {
        Assert.assertEquals(expected, putProductResponse.getStatusCode());
        return this;
    }

    private RestControllerTest productNameIs(String expected) {
        Assert.assertEquals(expected, getProductResponse.getBody().getName());
        return this;
    }

    private RestControllerTest totalPriceIs(BigDecimal expected) {
        Assert.assertEquals(expected, getOrderResponse.getBody().getTotalPrice());
        return this;
    }

    private RestControllerTest responseIdIs(Long expected) {
        Assert.assertEquals(expected, postResponse.getBody());
        return this;
    }

    private RestControllerTest numberOfOrdersIs(int expected) {
        Assert.assertEquals(expected, getOrdersResponse.getBody().size());
        return this;
    }

    private RestControllerTest and() {
        return this;
    }

    private RestControllerTest then() {
        return this;
    }

    private <T> HttpEntity<T> buildHttpEntity(T object) {
        return new HttpEntity<T>(object);
    }
}
