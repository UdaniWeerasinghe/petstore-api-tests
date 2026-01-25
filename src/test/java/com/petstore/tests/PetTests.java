package com.petstore.tests;

import com.petstore.base.BaseTest;
import com.petstore.model.Pet;
import com.petstore.utils.Endpoints;
import com.petstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Pet Store API")
@Feature("Pet Management")
public class PetTests extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Create Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a new pet can be created successfully")
    public void testCreatePet() {
        Pet pet = TestDataFactory.createRandomPet();

        Response response = givenRequest()
                .body(pet)
                .when()
                .post(Endpoints.PET)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo(pet.getName()))
                .body("status", equalTo(pet.getStatus()))
                .extract().response();

        Pet createdPet = response.as(Pet.class);
        assertThat(createdPet.getId()).isNotNull();
        assertThat(createdPet.getName()).isEqualTo(pet.getName());
        log.info("Created pet with ID: {}", createdPet.getId());
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Get Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a pet can be retrieved by ID")
    public void testGetPetById() {
        // First create a pet
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        // Then retrieve it
        Response response = givenRequest()
                .pathParam("petId", createdPet.getId())
                .when()
                .get(Endpoints.PET_BY_ID)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("name", equalTo(createdPet.getName()))
                .extract().response();

        Pet retrievedPet = response.as(Pet.class);
        assertThat(retrievedPet.getId()).isEqualTo(createdPet.getId());
        log.info("Retrieved pet: {}", retrievedPet.getName());
    }

    @Test(groups = {"regression"})
    @Story("Get Pet")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that getting a non-existent pet returns 404")
    public void testGetPetByIdNotFound() {
        long nonExistentId = 9999999999L;

        givenRequest()
                .pathParam("petId", nonExistentId)
                .when()
                .get(Endpoints.PET_BY_ID)
                .then()
                .spec(responseSpec)
                .statusCode(404);

        log.info("Verified 404 response for non-existent pet ID: {}", nonExistentId);
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Update Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a pet can be updated successfully")
    public void testUpdatePet() {
        // Create a pet first
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        // Update the pet
        createdPet.setName("Updated Pet Name");
        createdPet.setStatus(Pet.Status.sold.name());

        Response response = givenRequest()
                .body(createdPet)
                .when()
                .put(Endpoints.PET)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("name", equalTo("Updated Pet Name"))
                .body("status", equalTo("sold"))
                .extract().response();

        Pet updatedPet = response.as(Pet.class);
        assertThat(updatedPet.getName()).isEqualTo("Updated Pet Name");
        assertThat(updatedPet.getStatus()).isEqualTo("sold");
        log.info("Updated pet ID {} to name: {}", updatedPet.getId(), updatedPet.getName());
    }

    @Test(groups = {"regression"})
    @Story("Find Pets")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that pets can be found by status")
    public void testFindPetsByStatus() {
        // Create a pet with specific status
        Pet pet = TestDataFactory.createPetWithStatus("available");
        givenRequest()
                .body(pet)
                .post(Endpoints.PET);

        Response response = givenRequest()
                .queryParam("status", "available")
                .when()
                .get(Endpoints.PET_FIND_BY_STATUS)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", not(empty()))
                .body("status", everyItem(equalTo("available")))
                .extract().response();

        Pet[] pets = response.as(Pet[].class);
        assertThat(pets).isNotEmpty();
        assertThat(pets).allMatch(p -> "available".equals(p.getStatus()));
        log.info("Found {} available pets", pets.length);
    }

    @Test(groups = {"regression"})
    @Story("Find Pets")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that pets can be found by multiple statuses")
    public void testFindPetsByMultipleStatuses() {
        Response response = givenRequest()
                .queryParam("status", "available", "pending")
                .when()
                .get(Endpoints.PET_FIND_BY_STATUS)
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", not(empty()))
                .extract().response();

        Pet[] pets = response.as(Pet[].class);
        assertThat(pets).isNotEmpty();
        assertThat(pets).allMatch(p ->
                "available".equals(p.getStatus()) || "pending".equals(p.getStatus()));
        log.info("Found {} pets with available or pending status", pets.length);
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Delete Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a pet can be deleted successfully")
    public void testDeletePet() {
        // Create a pet first
        Pet pet = TestDataFactory.createRandomPet();
        Pet createdPet = givenRequest()
                .body(pet)
                .post(Endpoints.PET)
                .as(Pet.class);

        // Delete the pet
        givenRequest()
                .pathParam("petId", createdPet.getId())
                .when()
                .delete(Endpoints.PET_BY_ID)
                .then()
                .spec(responseSpec)
                .statusCode(200);

        // Verify the pet is deleted
        givenRequest()
                .pathParam("petId", createdPet.getId())
                .when()
                .get(Endpoints.PET_BY_ID)
                .then()
                .statusCode(404);

        log.info("Successfully deleted pet with ID: {}", createdPet.getId());
    }

    @Test(groups = {"regression"})
    @Story("Create Pet")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify pet creation with all status types")
    public void testCreatePetWithDifferentStatuses() {
        for (Pet.Status status : Pet.Status.values()) {
            Pet pet = TestDataFactory.createPetWithStatus(status.name());

            Response response = givenRequest()
                    .body(pet)
                    .when()
                    .post(Endpoints.PET)
                    .then()
                    .spec(responseSpec)
                    .statusCode(200)
                    .body("status", equalTo(status.name()))
                    .extract().response();

            log.info("Created pet with status: {}", status.name());
        }
    }
}
