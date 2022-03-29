package com.squadify.app.playlist;

import com.wrapper.spotify.model_objects.specification.Playlist;
import lombok.experimental.UtilityClass;

import static com.squadify.app.core.SquadifyTestFixture.someString;

@UtilityClass
public class PlaylistFixture {

    public static Playlist somePlaylist() {
        return new Playlist.Builder()
                .setUri(someString())
                .setId(someString())
                .build();
    }

}
