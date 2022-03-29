package com.squadify.app.auth;

import com.squadify.app.api.SpotifyApiFactory;
import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserDao;
import com.squadify.app.user.SquadifyUserFactory;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.squadify.app.api.SpotifyApiFactory.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SpotifyApiFactory spotifyApiFactory;
    private final SquadifyUserDao squadifyUserDao;
    private final SquadifyUserFactory squadifyUserFactory;

    public AuthorizationCodeUriRequest buildAuthorizationCodeUriRequest() {
        return spotifyApiFactory.spotifyApiWithoutUser(REDIRECT_URI)
                .authorizationCodeUri()
                .scope(
                        "playlist-modify-public " +
                        "playlist-read-private " +
                        "playlist-read-collaborative " +
                        "user-library-read " +
                        "user-read-private" +
                        "user-top-read "
                )
                .show_dialog(true)
                .build();
    }

    public SquadifyUser getSquadifyUserFromCode(String code, String redirect) throws ParseException, SpotifyWebApiException, IOException {
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFrom(code, redirect);
        User spotifyUser = spotifyUserFrom(authorizationCodeCredentials, redirect);
        return squadifyUserDao.findByUsername(spotifyUser.getId())
                .orElseGet(() -> squadifyUserFactory.create(spotifyUser, authorizationCodeCredentials));
    }

    public void refreshCredentialsForUser(SquadifyUser user) throws ParseException, SpotifyWebApiException, IOException {
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApiFactory.spotifyApiFrom(user).
                authorizationCodeRefresh()
                .build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
        updateAccessTokenForSquadifyUser(user, authorizationCodeCredentials.getAccessToken());
    }

    private AuthorizationCodeCredentials authorizationCodeCredentialsFrom(String code, String redirect) throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApiFactory.spotifyApiWithoutUser(redirect)
                .authorizationCode(code)
                .build();

        return authorizationCodeRequest.execute();
    }

    private User spotifyUserFrom(AuthorizationCodeCredentials credentials, String redirect) throws IOException, SpotifyWebApiException, ParseException {
        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApiFactory.spotifyApiFrom(credentials, redirect)
                .getCurrentUsersProfile()
                .build();

        return getCurrentUsersProfileRequest.execute();
    }

    private void updateAccessTokenForSquadifyUser(SquadifyUser user, String accessToken) {
        user.setAccessToken(accessToken);
        squadifyUserDao.save(user);
    }

}
