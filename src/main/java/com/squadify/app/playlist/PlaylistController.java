package com.squadify.app.playlist;

import com.squadify.app.auth.RequestContext;
import com.squadify.app.auth.SquadOwnerOnly;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.playlist.dto.PlaylistResponse;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUserDao;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("playlist")
public class PlaylistController extends SquadifyController {

    private final PlaylistCreator playlistCreator;

    @Autowired
    private RequestContext requestContext;

    @Autowired
    public PlaylistController(SquadDao squadDao, SquadifyUserDao squadifyUserDao, PlaylistCreator playlistCreator) {
        super(squadDao, squadifyUserDao);
        this.playlistCreator = playlistCreator;
    }

    @PostMapping("/create/{squadKey}")
    @SquadOwnerOnly
    public PlaylistResponse createSquadPlaylist() throws ParseException, SpotifyWebApiException, IOException {
        String url = playlistCreator.createPlaylist(requestContext.getSquad()).getId();
        return new PlaylistResponse(url);
    }

}
