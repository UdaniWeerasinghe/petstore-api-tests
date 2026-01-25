package com.petstore.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "system:env",
    "classpath:config.properties"
})
public interface Configuration extends Config {

    @Key("base.url")
    @DefaultValue("https://petstore.swagger.io/v2")
    String baseUrl();

    @Key("api.key")
    @DefaultValue("special-key")
    String apiKey();

    @Key("connection.timeout")
    @DefaultValue("30000")
    int connectionTimeout();

    @Key("socket.timeout")
    @DefaultValue("30000")
    int socketTimeout();

    @Key("log.all")
    @DefaultValue("true")
    boolean logAll();

    @Key("retry.count")
    @DefaultValue("3")
    int retryCount();
}
