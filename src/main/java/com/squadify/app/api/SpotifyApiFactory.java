package com.squadify.app.api;

import com.squadify.app.user.SquadifyUser;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.squadify.app.SquadifyApiConfig.BASE_URL;

@Service
public class SpotifyApiFactory {

    public static final String REDIRECT_URI = BASE_URL + ":8080/auth/callback";

    @Value("${oauth.client.id}")
    private String clientId;
    @Value("${oauth.client.secret}")
    private String clientSecret;

    public SpotifyApi spotifyApiWithoutUser(String redirect) {
        return builderWithClientCredentials(redirect).build();
    }

    public SpotifyApi spotifyApiFrom(SquadifyUser user) {
        return spotifyApiFrom(user.getAccessToken(), user.getRefreshToken(), REDIRECT_URI);
    }

    public SpotifyApi spotifyApiFrom(AuthorizationCodeCredentials credentials, String redirect) {
        return spotifyApiFrom(credentials.getAccessToken(), credentials.getRefreshToken(), redirect);
    }

    public SpotifyApi spotifyApiFrom(String accessToken, String refreshToken, String redirect) {
        return builderWithClientCredentials(redirect)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build();
    }

    private SpotifyApi.Builder builderWithClientCredentials(String redirect) {
        return SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(SpotifyHttpManager.makeUri(redirect));
    }

}
