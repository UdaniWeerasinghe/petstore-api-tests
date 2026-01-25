package com.petstore.base;

import com.petstore.config.Configuration;
import com.petstore.config.ConfigurationManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import static org.hamcrest.Matchers.lessThan;

public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static Configuration config;
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        config = ConfigurationManager.getConfiguration();
        RestAssured.baseURI = config.baseUrl();
        log.info("Base URI set to: {}", config.baseUrl());
    }

    @BeforeClass(alwaysRun = true)
    public void setupSpecifications() {
        requestSpec = createRequestSpecification();
        responseSpec = createResponseSpecification();
    }

    protected RequestSpecification createRequestSpecification() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addHeader("api_key", config.apiKey());

        if (config.logAll()) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }

    protected ResponseSpecification createResponseSpecification() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(30000L));

        if (config.logAll()) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }

    protected RequestSpecification givenRequest() {
        return RestAssured.given().spec(requestSpec);
    }
}
