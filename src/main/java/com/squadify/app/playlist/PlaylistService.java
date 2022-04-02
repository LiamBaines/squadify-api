package com.squadify.app.playlist;

import com.squadify.app.model.PlaylistDto;
import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistCreator playlistCreator;
    private final PlaylistResponseMapper responseMapper;
    private final SquadDao squadDao;

    public PlaylistDto createPlaylist(String squadId) throws ParseException, IOException, SpotifyWebApiException {
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        Playlist playlist = playlistCreator.createPlaylist(squad);
        return responseMapper.map(playlist);
    }
}
