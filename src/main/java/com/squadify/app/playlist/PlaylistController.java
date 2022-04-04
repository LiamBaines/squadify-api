package com.squadify.app.playlist;

import com.squadify.app.auth.RequestContext;
import com.squadify.app.auth.SquadOwnerOnly;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.model.PlaylistDto;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUserDao;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("v1/squads/{squadId/playlist")
public class PlaylistController extends SquadifyController {

    private final PlaylistService playlistService;

    @Autowired
    private RequestContext requestContext;

    @Autowired
    public PlaylistController(SquadDao squadDao, SquadifyUserDao squadifyUserDao, PlaylistService playlistService) {
        super(squadDao, squadifyUserDao);
        this.playlistService = playlistService;
    }

    @PostMapping
    @SquadOwnerOnly
    public PlaylistDto createSquadPlaylist(@PathVariable String squadId) throws ParseException, SpotifyWebApiException, IOException {
        return playlistService.createPlaylist(squadId);
    }

}
