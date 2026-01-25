package com.petstore.utils;

import com.petstore.model.*;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    private TestDataFactory() {
        // Private constructor to prevent instantiation
    }

    public static Pet createRandomPet() {
        return Pet.builder()
                .id(generateRandomId())
                .name(faker.animal().name())
                .category(createRandomCategory())
                .photoUrls(List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                .tags(List.of(createRandomTag(), createRandomTag()))
                .status(Pet.Status.available.name())
                .build();
    }

    public static Pet createPetWithStatus(String status) {
        Pet pet = createRandomPet();
        pet.setStatus(status);
        return pet;
    }

    public static Category createRandomCategory() {
        return Category.builder()
                .id(generateRandomId())
                .name(faker.animal().genus())
                .build();
    }

    public static Tag createRandomTag() {
        return Tag.builder()
                .id(generateRandomId())
                .name(faker.lorem().word())
                .build();
    }

    public static Order createRandomOrder() {
        return Order.builder()
                .id(generateRandomId())
                .petId(generateRandomId())
                .quantity(faker.number().numberBetween(1, 10))
                .shipDate(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 30))
                        .format(DateTimeFormatter.ISO_DATE_TIME))
                .status(Order.Status.placed.name())
                .complete(false)
                .build();
    }

    public static Order createOrderForPet(Long petId) {
        Order order = createRandomOrder();
        order.setPetId(petId);
        return order;
    }

    public static User createRandomUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        return User.builder()
                .id(generateRandomId())
                .username(firstName.toLowerCase() + lastName.toLowerCase() + faker.number().numberBetween(1, 999))
                .firstName(firstName)
                .lastName(lastName)
                .email(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com")
                .password(faker.internet().password(8, 16, true, true))
                .phone(faker.phoneNumber().cellPhone())
                .userStatus(1)
                .build();
    }

    public static List<User> createRandomUsers(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> createRandomUser())
                .collect(Collectors.toList());
    }

    private static Long generateRandomId() {
        return ThreadLocalRandom.current().nextLong(100000, 999999);
    }
}
