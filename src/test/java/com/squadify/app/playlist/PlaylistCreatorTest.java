package com.squadify.app.playlist;

import com.squadify.app.api.SpotifyApiFactory;
import com.squadify.app.auth.AuthService;
import com.squadify.app.squad.SquadService;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import com.wrapper.spotify.requests.data.follow.FollowPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.UploadCustomPlaylistCoverImageRequest;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.Supplier;

import static com.squadify.app.playlist.PlaylistFixture.somePlaylist;
import static com.squadify.app.playlist.TrackFixture.someTracks;
import static com.squadify.app.squad.SquadFixture.someSquad;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaylistCreatorTest {

    @Mock
    private AuthService authServiceMock;

    @Mock
    private SquadService squadServiceMock;

    @Mock
    private SpotifyApiFactory spotifyApiFactoryMock;

    @InjectMocks
    private PlaylistCreator underTest;

    @Mock
    private TrackFetcher trackFetcherMock;

    @Mock
    private TrackFetcherFactory trackFetcherFactoryMock;

    @Mock
    private CommonTrackFinder commonTrackFinderMock;

    @Mock
    private SpotifyApi spotifyApiMock;

    @Mock(answer = Answers.RETURNS_SELF)
    private CreatePlaylistRequest.Builder createPlaylistRequestBuilderMock;

    @Mock
    private CreatePlaylistRequest createPlaylistRequestMock;

    @Mock(answer = Answers.RETURNS_SELF)
    private UploadCustomPlaylistCoverImageRequest.Builder uploadCustomPlaylistCoverImageRequestBuilderMock;

    @Mock
    private UploadCustomPlaylistCoverImageRequest uploadCustomPlaylistCoverImageRequestMock;

    @Mock(answer = Answers.RETURNS_SELF)
    private AddItemsToPlaylistRequest.Builder addItemsToPlaylistRequestBuilderMock;

    @Mock
    private AddItemsToPlaylistRequest addItemsToPlaylistRequestMock;

    @Mock(answer = Answers.RETURNS_SELF)
    private FollowPlaylistRequest.Builder followPlaylistRequestBuilderMock;

    @Mock
    private FollowPlaylistRequest followPlaylistRequestMock;

    @BeforeEach
    void setUp() throws Exception {
        when(trackFetcherFactoryMock.trackFetcherFrom(any()))
                .thenReturn(trackFetcherMock);
        when(spotifyApiFactoryMock.spotifyApiFrom(any()))
                .thenAnswer(inv -> spotifyApiMock);

        mockRequestAndBuilder(() -> spotifyApiMock.createPlaylist(any(), any()), createPlaylistRequestBuilderMock, createPlaylistRequestMock);
        when(createPlaylistRequestMock.execute()).thenReturn(somePlaylist());
        mockRequestAndBuilder(() -> spotifyApiMock.uploadCustomPlaylistCoverImage(any()), uploadCustomPlaylistCoverImageRequestBuilderMock, uploadCustomPlaylistCoverImageRequestMock);
        mockRequestAndBuilder(() -> spotifyApiMock.addItemsToPlaylist(any(), any(String[].class)), addItemsToPlaylistRequestBuilderMock, addItemsToPlaylistRequestMock);
        mockRequestAndBuilder(() -> spotifyApiMock.followPlaylist(any(), eq(true)), followPlaylistRequestBuilderMock, followPlaylistRequestMock);
    }

    @Test
    void canCreatePlaylist() throws ParseException, SpotifyWebApiException, IOException {
        // given
        when(trackFetcherMock.getTracksForUser()).thenAnswer(inv -> someTracks());
        when(commonTrackFinderMock.findCommonTracks(any())).thenReturn(someTracks().toArray(String[]::new));

        underTest.createPlaylist(someSquad(3));

    }

    private <T, BT extends AbstractDataRequest.Builder<T, ?>> void mockRequestAndBuilder(Supplier<AbstractDataRequest.Builder<T, BT>> supplier, AbstractDataRequest.Builder<T, BT> requestBuilderMock, AbstractDataRequest<T> requestMock) throws Exception {
        when(supplier.get())
                .thenReturn(requestBuilderMock);
        when(requestBuilderMock.build())
                .thenReturn(requestMock);
    }

}