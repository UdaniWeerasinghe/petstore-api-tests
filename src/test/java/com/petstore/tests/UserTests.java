package com.petstore.tests;

import com.petstore.base.BaseTest;
import com.petstore.model.User;
import com.petstore.utils.Endpoints;
import com.petstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Pet Store API")
@Feature("User Management")
public class UserTests extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Create User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a new user can be created successfully")
    public void testCreateUser() {
        User user = TestDataFactory.createRandomUser();

        Response response = givenRequest()
                .body(user)
                .when()
                .post(Endpoints.USER)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .body("message", notNullValue())
                .extract().response();

        log.info("Created user with username: {}", user.getUsername());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Get User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can be retrieved by username")
    public void testGetUserByUsername() {
        // Create a user first
        User user = TestDataFactory.createRandomUser();
        givenRequest()
                .body(user)
                .post(Endpoints.USER);

        // Retrieve the user
        Response response = givenRequest()
                .pathParam("username", user.getUsername())
                .when()
                .get(Endpoints.USER_BY_USERNAME)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("username", equalTo(user.getUsername()))
                .body("firstName", equalTo(user.getFirstName()))
                .body("lastName", equalTo(user.getLastName()))
                .body("email", equalTo(user.getEmail()))
                .extract().response();

        User retrievedUser = response.as(User.class);
        assertThat(retrievedUser.getUsername()).isEqualTo(user.getUsername());
        log.info("Retrieved user: {}", retrievedUser.getUsername());
    }

    @Test(groups = {"regression"})
    @Story("Get User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that getting a non-existent user returns 404")
    public void testGetUserByUsernameNotFound() {
        String nonExistentUsername = "nonexistentuser99999";

        givenRequest()
                .pathParam("username", nonExistentUsername)
                .when()
                .get(Endpoints.USER_BY_USERNAME)
                .then()
                .spec(responseSpec)
                .statusCode(404);

        log.info("Verified 404 response for non-existent username: {}", nonExistentUsername);
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Update User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can be updated successfully")
    public void testUpdateUser() {
        // Create a user first
        User user = TestDataFactory.createRandomUser();
        givenRequest()
                .body(user)
                .post(Endpoints.USER);

        // Update the user
        user.setFirstName("UpdatedFirstName");
        user.setLastName("UpdatedLastName");
        user.setEmail("updated.email@example.com");

        givenRequest()
                .pathParam("username", user.getUsername())
                .body(user)
                .when()
                .put(Endpoints.USER_BY_USERNAME)
                .then()
                .spec(responseSpec)
                .statusCode(200);

        // Verify the update
        Response response = givenRequest()
                .pathParam("username", user.getUsername())
                .when()
                .get(Endpoints.USER_BY_USERNAME)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("firstName", equalTo("UpdatedFirstName"))
                .body("lastName", equalTo("UpdatedLastName"))
                .body("email", equalTo("updated.email@example.com"))
                .extract().response();

        log.info("Updated user: {}", user.getUsername());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Delete User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can be deleted successfully")
    public void testDeleteUser() {
        // Create a user first
        User user = TestDataFactory.createRandomUser();
        givenRequest()
                .body(user)
                .post(Endpoints.USER);

        // Delete the user
        givenRequest()
                .pathParam("username", user.getUsername())
                .when()
                .delete(Endpoints.USER_BY_USERNAME)
                .then()
                .spec(responseSpec)
                .statusCode(200);

        // Verify the user is deleted
        givenRequest()
                .pathParam("username", user.getUsername())
                .when()
                .get(Endpoints.USER_BY_USERNAME)
                .then()
                .statusCode(404);

        log.info("Successfully deleted user: {}", user.getUsername());
    }

    @Test(groups = {"regression"})
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that multiple users can be created with array")
    public void testCreateUsersWithArray() {
        List<User> users = TestDataFactory.createRandomUsers(3);

        givenRequest()
                .body(users)
                .when()
                .post(Endpoints.USER_CREATE_WITH_ARRAY)
                .then()
                .spec(responseSpec)
                .statusCode(200);

        // Verify each user was created
        for (User user : users) {
            givenRequest()
                    .pathParam("username", user.getUsername())
                    .when()
                    .get(Endpoints.USER_BY_USERNAME)
                    .then()
                    .statusCode(200)
                    .body("username", equalTo(user.getUsername()));
        }

        log.info("Created {} users with array", users.size());
    }

    @Test(groups = {"regression"})
    @Story("Create User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that multiple users can be created with list")
    public void testCreateUsersWithList() {
        List<User> users = TestDataFactory.createRandomUsers(2);

        givenRequest()
                .body(users)
                .when()
                .post(Endpoints.USER_CREATE_WITH_LIST)
                .then()
                .spec(responseSpec)
                .statusCode(200);

        log.info("Created {} users with list", users.size());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("User Authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can login successfully")
    public void testUserLogin() {
        // Create a user first
        User user = TestDataFactory.createRandomUser();
        givenRequest()
                .body(user)
                .post(Endpoints.USER);

        // Login
        Response response = givenRequest()
                .queryParam("username", user.getUsername())
                .queryParam("password", user.getPassword())
                .when()
                .get(Endpoints.USER_LOGIN)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .header("X-Rate-Limit", notNullValue())
                .header("X-Expires-After", notNullValue())
                .body("message", containsString("logged in user session"))
                .extract().response();

        log.info("User {} logged in successfully", user.getUsername());
    }

    @Test(groups = {"regression"})
    @Story("User Authentication")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a user can logout successfully")
    public void testUserLogout() {
        givenRequest()
                .when()
                .get(Endpoints.USER_LOGOUT)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("message", equalTo("ok"));

        log.info("User logged out successfully");
    }
}
