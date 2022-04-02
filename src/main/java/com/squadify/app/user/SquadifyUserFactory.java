package com.squadify.app.user;

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SquadifyUserFactory {

    private final SquadifyUserDao squadifyUserDao;

    public SquadifyUser create(User user, AuthorizationCodeCredentials credentials) {
        SquadifyUser squadifyUser = new SquadifyUser.SquadifyUserBuilder()
                .username(user.getId())
                .name(user.getDisplayName())
                .accessToken(credentials.getAccessToken())
                .refreshToken(credentials.getRefreshToken())
                .build();

        squadifyUserDao.save(squadifyUser);

        return squadifyUser;
    }

}
