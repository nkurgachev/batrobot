package com.batrobot.steam.infrastructure.config;

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "steam.api")
@Setter
public class SteamConfig {

    private String token;

    @Bean
    public SteamWebApiClient steamWebApiClient() {
        return new SteamWebApiClient.SteamWebApiClientBuilder(token).build();
    }
}
