package com.petstore.utils;

public final class Endpoints {

    private Endpoints() {
        // Private constructor to prevent instantiation
    }

    // Pet endpoints
    public static final String PET = "/pet";
    public static final String PET_BY_ID = "/pet/{petId}";
    public static final String PET_FIND_BY_STATUS = "/pet/findByStatus";
    public static final String PET_FIND_BY_TAGS = "/pet/findByTags";
    public static final String PET_UPLOAD_IMAGE = "/pet/{petId}/uploadImage";

    // Store endpoints
    public static final String STORE_INVENTORY = "/store/inventory";
    public static final String STORE_ORDER = "/store/order";
    public static final String STORE_ORDER_BY_ID = "/store/order/{orderId}";

    // User endpoints
    public static final String USER = "/user";
    public static final String USER_BY_USERNAME = "/user/{username}";
    public static final String USER_CREATE_WITH_ARRAY = "/user/createWithArray";
    public static final String USER_CREATE_WITH_LIST = "/user/createWithList";
    public static final String USER_LOGIN = "/user/login";
    public static final String USER_LOGOUT = "/user/logout";
}
