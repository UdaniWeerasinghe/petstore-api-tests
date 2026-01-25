package com.petstore.tests;

import com.petstore.base.BaseTest;
import com.petstore.model.Order;
import com.petstore.model.Pet;
import com.petstore.utils.Endpoints;
import com.petstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Pet Store API")
@Feature("Store Management")
public class StoreTests extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Get Inventory")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that store inventory can be retrieved")
    public void testGetInventory() {
        Response response = givenRequest()
                .when()
                .get(Endpoints.STORE_INVENTORY)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", not(anEmptyMap()))
                .extract().response();

        Map<String, Integer> inventory = response.as(Map.class);
        assertThat(inventory).isNotEmpty();
        log.info("Retrieved inventory with {} status types", inventory.size());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Create Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an order can be placed successfully")
    public void testCreateOrder() {
        // First create a pet
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        // Create order for the pet
        Order order = TestDataFactory.createOrderForPet(createdPet.getId());

        Response response = givenRequest()
                .body(order)
                .when()
                .post(Endpoints.STORE_ORDER)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", notNullValue())
                .body("status", equalTo(order.getStatus()))
                .extract().response();

        Order createdOrder = response.as(Order.class);
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getPetId()).isEqualTo(createdPet.getId());
        log.info("Created order with ID: {} for pet ID: {}", createdOrder.getId(), createdOrder.getPetId());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Get Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an order can be retrieved by ID")
    public void testGetOrderById() {
        // Create a pet first
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        // Create an order
        Order order = TestDataFactory.createOrderForPet(createdPet.getId());
        Order createdOrder = givenRequest()
                .body(order)
                .post(Endpoints.STORE_ORDER)
                .as(Order.class);

        // Retrieve the order
        Response response = givenRequest()
                .pathParam("orderId", createdOrder.getId())
                .when()
                .get(Endpoints.STORE_ORDER_BY_ID)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();

        Order retrievedOrder = response.as(Order.class);
        assertThat(retrievedOrder.getId()).isEqualTo(createdOrder.getId());
        assertThat(retrievedOrder.getPetId()).isEqualTo(createdOrder.getPetId());
        log.info("Retrieved order with ID: {}", retrievedOrder.getId());
    }

    @Test(groups = {"regression"})
    @Story("Get Order")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that getting a non-existent order returns 404")
    public void testGetOrderByIdNotFound() {
        long nonExistentId = 9999999999L;

        givenRequest()
                .pathParam("orderId", nonExistentId)
                .when()
                .get(Endpoints.STORE_ORDER_BY_ID)
                .then()
                .spec(responseSpec)
                .statusCode(404);

        log.info("Verified 404 response for non-existent order ID: {}", nonExistentId);
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Delete Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an order can be deleted successfully")
    public void testDeleteOrder() {
        // Create a pet first
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        // Create an order
        Order order = TestDataFactory.createOrderForPet(createdPet.getId());
        Order createdOrder = givenRequest()
                .body(order)
                .post(Endpoints.STORE_ORDER)
                .as(Order.class);

        // Delete the order
        givenRequest()
                .pathParam("orderId", createdOrder.getId())
                .when()
                .delete(Endpoints.STORE_ORDER_BY_ID)
                .then()
                .spec(responseSpec)
                .statusCode(200);

        // Verify the order is deleted
        givenRequest()
                .pathParam("orderId", createdOrder.getId())
                .when()
                .get(Endpoints.STORE_ORDER_BY_ID)
                .then()
                .statusCode(404);

        log.info("Successfully deleted order with ID: {}", createdOrder.getId());
    }

    @Test(groups = {"regression"})
    @Story("Create Order")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify order creation with different quantities")
    public void testCreateOrderWithDifferentQuantities() {
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        int[] quantities = {1, 5, 10, 100};

        for (int quantity : quantities) {
            Order order = TestDataFactory.createOrderForPet(createdPet.getId());
            order.setQuantity(quantity);

            Response response = givenRequest()
                    .body(order)
                    .when()
                    .post(Endpoints.STORE_ORDER)
                    .then()
                    .spec(responseSpec)
                    .statusCode(200)
                    .body("quantity", equalTo(quantity))
                    .extract().response();

            log.info("Created order with quantity: {}", quantity);
        }
    }
}
