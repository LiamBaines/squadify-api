package com.squadify.app.playlist;

import com.squadify.app.model.PlaylistDto;
import org.springframework.stereotype.Service;


@Service
public class PlaylistResponseMapper {

    public PlaylistDto map(com.wrapper.spotify.model_objects.specification.Playlist playlist) {
        PlaylistDto playlistDto = new PlaylistDto();
        playlistDto.setUrl(playlist.getId());
        return playlistDto;
    }

    public PlaylistDto map(Playlist playlist) {
        PlaylistDto playlistDto = new PlaylistDto();
        playlistDto.setUrl(playlist.getUrl());
        return playlistDto;
    }
}
