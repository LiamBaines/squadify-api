package com.squadify.app.playlist;

import com.squadify.app.api.SpotifyApiFactory;
import com.squadify.app.auth.AuthService;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.AbstractModelObject;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.squadify.app.core.SquadifyUtils.writeTracksToFile;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class TrackFetcher {

    private static final int PAGE_SIZE = 50;

    private final AuthService authService;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SquadDao squadDao;
    private final SquadifyUser user;

    Set<String> getTracksForUser() throws ParseException, SpotifyWebApiException, IOException {
        authService.refreshCredentialsForUser(user);

        Set<String> tracks = new LinkedHashSet<>();
        tracks.addAll(getSavedTracks());
        tracks.addAll(getTopTracks());
        tracks.addAll(getTracksFromCreatedPlaylists());

        writeTracksToFile(user.getUsername(), tracks);

        return tracks;
    }

    private Set<String> getSavedTracks() throws ParseException, SpotifyWebApiException, IOException {
        return execute(this::savedTracksRequest).stream().map(SavedTrack::getTrack).map(Track::getUri).collect(toSet());
    }

    private Set<String> getTopTracks() throws ParseException, SpotifyWebApiException, IOException {
        Set<String> tracks = new LinkedHashSet<>();
        tracks.addAll(execute(offset -> topTracksRequest(offset, "short_term")).stream().map(Track::getUri).collect(toSet()));
        tracks.addAll(execute(offset -> topTracksRequest(offset, "medium_term")).stream().map(Track::getUri).collect(toSet()));
        tracks.addAll(execute(offset -> topTracksRequest(offset, "long_term")).stream().map(Track::getUri).collect(toSet()));
        return tracks;
    }


    private Set<String> getTracksFromCreatedPlaylists() throws ParseException, SpotifyWebApiException, IOException {
        List<String> squadifyPlaylists = squadDao.findByOwner(user).stream().map(squad -> squad.getPlaylist().getUrl()).collect(toList());
        return execute(this::playlistsRequest).stream()
                .filter(this::ownedByUser)
                .filter(squad -> !squadifyPlaylists.contains(squad.getUri()))
                .map(this::getPlaylistItems)
                .flatMap(Set::stream)
                .collect(toSet());
    }

    private Set<String> getPlaylistItems(PlaylistSimplified playlist) {
       try {
           Set<PlaylistTrack> set = execute(offset -> playlistItemsRequest(playlist, offset));
           return set.stream()
                   .filter(item -> !item.getIsLocal())
                   .map(PlaylistTrack::getTrack)
                   .map(IPlaylistItem::getUri)
                   .collect(toSet());
       }
       catch (Exception ignore) {
           return Collections.emptySet();
       }
    }

    private <T extends AbstractModelObject, R extends AbstractDataRequest<Paging<T>>> Set<T> execute(Function<Integer, R> requestFunction) throws ParseException, SpotifyWebApiException, IOException {
        Paging<T> page;
        Set<T> tracks = new LinkedHashSet<>();
        int offset = 0;

        do {
            R request = requestFunction.apply(offset);
            page = request.execute();
            tracks.addAll(asList(page.getItems()));
            offset += PAGE_SIZE;

        } while (page.getNext() != null);

        return tracks;
    }

    private GetUsersSavedTracksRequest savedTracksRequest(int offset) {
        return spotifyApiFactory.spotifyApiFrom(user).getUsersSavedTracks()
                .limit(PAGE_SIZE)
                .offset(offset)
                .build();
    }

    private GetUsersTopTracksRequest topTracksRequest(int offset, String timeRange) {
        return spotifyApiFactory.spotifyApiFrom(user).getUsersTopTracks()
                .time_range(timeRange)
                .limit(PAGE_SIZE)
                .offset(offset)
                .build();
    }

    private GetListOfCurrentUsersPlaylistsRequest playlistsRequest(int offset) {
        return spotifyApiFactory.spotifyApiFrom(user).getListOfCurrentUsersPlaylists()
                .limit(PAGE_SIZE)
                .offset(offset)
                .build();
    }

    private boolean ownedByUser(PlaylistSimplified playlist) {
        return playlist.getOwner().getId().equals(user.getUsername());
    }

    private GetPlaylistsItemsRequest playlistItemsRequest(PlaylistSimplified playlist, int offset) {
        return spotifyApiFactory.spotifyApiFrom(user).getPlaylistsItems(playlist.getId())
                .limit(PAGE_SIZE)
                .offset(offset)
                .build();
    }

    // get saved albums -> get tracks from each album

    // get user playlists -> get tracks from each playlist

    // get top artists -> get top tracks for each artist

    // final string is from Track::getUri --> spotify:track:330pSk...

}
