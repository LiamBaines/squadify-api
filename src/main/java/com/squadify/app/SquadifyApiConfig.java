package com.squadify.app;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@lombok.Value
public class SquadifyApiConfig {

    public SquadifyApiConfig(@Value("${squadify.dev.api.url}") String apiUrl,
                             @Value("${squadify.dev.client.url}") String clientUrl) {
        this.apiUrl = apiUrl;
        this.clientUrl = clientUrl;
    }

    String apiUrl;

    String clientUrl;

    public String getRedirectUrl() {
        return apiUrl + "/auth/callback";
    }

}