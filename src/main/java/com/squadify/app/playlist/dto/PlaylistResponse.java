package com.squadify.app.playlist.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@RequiredArgsConstructor
public class PlaylistResponse implements Serializable {

    String playlistUrl;

}
