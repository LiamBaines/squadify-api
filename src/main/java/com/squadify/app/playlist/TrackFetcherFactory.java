package com.squadify.app.playlist;

import com.squadify.app.api.SpotifyApiFactory;
import com.squadify.app.auth.AuthService;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrackFetcherFactory {

    private final AuthService authService;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SquadDao squadDao;

    TrackFetcher trackFetcherFrom(SquadifyUser user) {
        return new TrackFetcher(authService, spotifyApiFactory, squadDao, user);
    }

}
